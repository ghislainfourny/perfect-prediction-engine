# Large-scale non-Nashian model generation for quantum physics

## Create Games

### Setup Envirionment

This Repository provides a Dockerfile that can be used in order to setup the envirionment needed to run the code. Furthermore, there is a task file for VS Code that can be used to build and run the Docker container.

```shell
docker build -t "quantum-game-generator", "."
docker run -dit --name "quantum-game-generator" -v {path of ource code} -v {path for output} "quantum-game-generator"
```

- `{path of source code}`: Used to mount source code into Docker container
- `{path for output}`: Where the game creator stores the output

> Depending on how many games are getting created, the output location should provoide enough storage capacity.

### Running Python code to generate games

```python
python GameCreator.py
```

In the `GameCretor.py` all the configurations can be made in order to change how many games are created.

- `NUM_WORKERS` specifies how many parallel processes will be run in the sparc context
- `WORKER_LOAD` specifies how many games each worker creats
- `NUM_REPETITIONS` specifeis how many times each worker creates the specified amount of games

```python
NUM_WORKERS = 10
WORKER_LOAD = 50000
NUM_REPETITIONS = 1000
```

This configuration will create 500'000'000 games

### Running Java code to solve generated games

**Build without running tests:**

```shell
cd ${workspaceFolder}/perfect-prediction-engine/perfect-prediction-engine-json-input
mvn clean install -DskipTests
```

**Run built code:**

```shell
java -jar ${workspaceFolder}/perfect-prediction-engine/perfect-prediction-engine-json-input/target/perfectpredictionengine-0.1-jar-with-dependencies.jar
```

## License

This repository is a fork of the [perfect-prediction-engine](https://github.com/ghislainfourny/perfect-prediction-engine/tree/master), which is licensed under the Apache License 2.0. The fork was created on May 12, 2023. Please see the [LICENSE](https://github.com/ghislainfourny/perfect-prediction-engine/blob/master/LICENSE) file for more information about the licensing terms.
