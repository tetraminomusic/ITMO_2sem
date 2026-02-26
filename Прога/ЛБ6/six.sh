#!/bin/bash

export LAB_DATA_PATH="data.json"
if [ ! -f "$LAB_DATA_PATH" ]; then echo "{}" > "$LAB_DATA_PATH"; fi

tmux new-session -d -s lab6 "java --add-opens java.base/java.time=ALL-UNNAMED -jar server-all.jar"
tmux split-window -h "java -jar client-all.jar"
tmux attach-session -t lab6
