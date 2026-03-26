#!/bin/bash

export DB_HOST="127.0.0.1"
export DB_PORT="5433"
export DB_NAME="studs"
export DB_USER="postgres"
export DB_PASS="12345"

java --add-opens java.base/java.time=ALL-UNNAMED -jar server-all.jar
