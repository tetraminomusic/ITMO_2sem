#!/bin/bash

export DB_HOST="127.0.0.1"
export DB_PORT="5433"
export DB_NAME="studs"
export DB_USER="postgres"         
export DB_PASS="12345"            

SESSION="lab7"

tmux kill-session -t $SESSION 2>/dev/null

tmux new-session -d -s $SESSION "bash -c 'java --add-opens java.base/java.time=ALL-UNNAMED -jar server-all.jar; exec bash'"

sleep 2

tmux split-window -h "bash -c 'java -jar client-all.jar; exec bash'"

tmux attach-session -t $SESSION