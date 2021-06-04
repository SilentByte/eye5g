#!/usr/bin/env bash

set -euo pipefail

DIR="$( cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null && pwd)"

"$DIR/download_weights.sh"

RUST_LOG=debug RUST_BACKTRACE=1 cargo run --release -- \
    --host "0.0.0.0" \
    --port 9000 \
    --model-labels "$DIR/darknet/coco.names" \
    --model-cfg "$DIR/darknet/yolov4.cfg" \
    --model-weights "$DIR/darknet/yolov4.weights"
