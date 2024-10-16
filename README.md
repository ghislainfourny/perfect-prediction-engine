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

Copyright Ramon Gomm, Ghislain Fourny, Gustavo Alonso, 2019-2020. All rights reserved.
