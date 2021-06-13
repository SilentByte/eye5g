/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

use std::net::{
    TcpListener,
    TcpStream,
};
use std::path::Path;
use std::path::PathBuf;
use std::sync::{
    Arc,
    Mutex,
};
use std::thread::spawn;
use std::time::Instant;

use argh::FromArgs;
use serde::Serialize;
use tungstenite::server::accept;
use tungstenite::Message;

/// Eye5G Server.
#[derive(Debug, Clone, FromArgs)]
struct Args {
    /// the hostname on which the server listens.
    #[argh(option, default = r#""localhost".into()"#)]
    host: String,

    /// the server's port.
    #[argh(option, default = "9000")]
    port: u16,

    /// the model configuration file (*.cfg).
    #[argh(option)]
    model_cfg: PathBuf,

    /// the label definition file (*.names).
    #[argh(option)]
    model_labels: PathBuf,

    /// the model weights file (*.weights).
    #[argh(option)]
    model_weights: PathBuf,

    /// the objectness threshold (default: 0.9).
    #[argh(option, default = "0.9")]
    objectness_threshold: f32,

    /// the class probability threshold (default 0.9).
    #[argh(option, default = "0.9")]
    class_prob_threshold: f32,
}

#[derive(Debug, Serialize)]
struct BBox {
    x: f32,
    y: f32,
    width: f32,
    height: f32,
}

#[derive(Debug, Serialize)]
struct Eye5GObject {
    label: String,
    probability: f32,
    bbox: BBox,
}

struct Eye5GNetwork {
    network: darknet::Network,
    labels: Vec<String>,
}

impl Eye5GNetwork {
    pub fn from_files(
        model_cfg: &Path,
        model_labels: &Path,
        model_weights: &Path,
    ) -> anyhow::Result<Self> {
        let labels: Vec<String> = std::fs::read_to_string(model_labels)?
            .lines()
            .map(ToOwned::to_owned)
            .collect();

        let network = darknet::Network::load(model_cfg, Some(model_weights), false)?;

        Ok(Self { network, labels })
    }

    pub fn detect_objects<'a, I>(&mut self, image: I) -> Vec<Eye5GObject>
    where
        I: darknet::IntoCowImage<'a>,
    {
        // TODO: Give access to parameters from outside.
        let objectness_threshold = 0.9;
        let class_prob_threshold = 0.9;
        let detections = self
            .network
            .predict(image, 0.25, 0.5, 0.45, true)
            .iter()
            .filter(|det| det.objectness() > objectness_threshold)
            .flat_map(|det| {
                det.best_class(Some(class_prob_threshold))
                    .map(|(class_index, prob)| (det, &self.labels[class_index], prob))
            })
            .map(|(det, label, probability)| {
                let bbox = det.bbox();
                Eye5GObject {
                    label: label.clone(),
                    probability,
                    bbox: BBox {
                        x: bbox.x,
                        y: bbox.y,
                        width: bbox.w,
                        height: bbox.h,
                    },
                }
            })
            .collect();

        detections
    }
}

fn handle_connection(
    stream: TcpStream,
    network: Arc<Mutex<Eye5GNetwork>>,
    _args: Args,
) -> anyhow::Result<()> {
    log::info!("Handling incoming connection");

    let mut ws = accept(stream)?;
    loop {
        let message = match ws.read_message() {
            Ok(message) => message,
            Err(tungstenite::Error::ConnectionClosed) => break,
            e => e?,
        };

        match message {
            Message::Text(_) => {
                anyhow::bail!("Unexpected text message, binary image data expected");
            }
            Message::Binary(message) => {
                log::info!("Received binary message, running object detection");

                let start_time = Instant::now();
                let image = image::load_from_memory(&message)?.to_rgb8();
                let detections = {
                    // TODO: Pass args.
                    (*network.lock().unwrap()).detect_objects(&image)
                };
                let duration = start_time.elapsed();

                log::debug!("{:#?}", detections);
                log::debug!("Inference took {:.4}s", duration.as_secs_f64());

                ws.write_message(Message::Text(serde_json::to_string(&detections).unwrap()))?;
            }
            _ => (),
        }
    }

    Ok(())
}

fn main() -> anyhow::Result<()> {
    env_logger::init();

    let args: Args = argh::from_env();

    log::info!("Loading network...");
    let network = Arc::new(Mutex::new(Eye5GNetwork::from_files(
        &args.model_cfg,
        &args.model_labels,
        &args.model_weights,
    )?));

    log::info!("Starting server at {}:{}...", args.host, args.port);
    let server = TcpListener::bind(format!("{}:{}", args.host, args.port))?;
    for stream in server.incoming() {
        let args = args.clone();
        let network = Arc::clone(&network);

        spawn(move || match stream {
            Ok(stream) => {
                if let Err(e) = handle_connection(stream, network, args) {
                    log::error!("Failed to handle connection successfully: {}", e)
                }
            }
            Err(e) => log::error!("Incoming stream is erroneous: {}", e),
        });
    }

    Ok(())
}
