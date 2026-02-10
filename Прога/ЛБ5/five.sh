#!/bin/bash

export LAB_DATA_PATH="data.json"


if [ ! -f "$LAB_DATA_PATH" ]; then
	echo "{}" > "$LAB_DATA_PATH"
fi

java -Dfile.encoding=UTF-8 -jar Lab5_bms.jar
