#!/usr/bin/env bash

set -euo pipefail

DIR="$( cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null && pwd)"
MODEL_WEIGHTS="$DIR/darknet/yolov4.weights"

if [[ ! -f "$MODEL_WEIGHTS" ]]; then
    echo 'Downloading pre-trained model weights...'
    wget -q --show-progress -O "$MODEL_WEIGHTS" \
        'https://github.com/AlexeyAB/darknet/releases/download/darknet_yolo_v3_optimal/yolov4.weights'
else
    echo 'Pre-trained model weights already exist:'
    echo "$MODEL_WEIGHTS"
fi
