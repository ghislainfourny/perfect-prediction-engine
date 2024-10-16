# Perfect Prediction Engine
First implementation of the Perfect Prediction Engine

Prerequisite: Maven and Java 11 (will not work with 8)

Creating games with python:

    cd python/src
    python GameCreator.py
    
It will put the games in /tmp/generated/games1. You can change this directory, as well as the parameters, in python/src/GameCreator.py.

Solving games with JAVA:

    cd java
    mvn package
    java -jar target/perfectpredictionengine-0.1-jar-with-dependencies.jar /tmp/generated/games1 /tmp/labeled/games
    
A copy of the games extended with Nash and PTE resolution is in /tmp/labeled/games.

Building the histogram

Download the [standalone RumbleDB jar](https://github.com/RumbleDB/rumble/releases/download/v1.21.0/rumbledb-1.21.0-standalone.jar) and put it in the jsoniq directory.

    cd jsoniq
    java -jar rumbledb-1.21.0-standalone.jar run histogram.jq --output-format csv --number-of-output-partitions 1 --output-format-option:header true --output-path /tmp/histogram --overwrite yes
    
The output csv file will be in /tmp/histogram in a subdirectory.

Copyright Ramon Gomm, Luc Stoffer, Ghislain Fourny 2019-2024. All rights reserved.
