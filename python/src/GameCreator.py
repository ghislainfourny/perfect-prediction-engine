import os
import time
from datetime import datetime
from pyspark import SparkConf, SparkContext
from SimpleGame import SimpleGame
from ComplexGame import ComplexGame


conf = SparkConf().setAppName("GameCreator").setMaster("local[*]")
sc = SparkContext(conf=conf)
current_datetime = datetime.now().strftime("%Y%m%d%H%M%S")
file_path = os.environ.get("OUTPUT_GENERATED") + current_datetime + "/"

# num_workers * worker_load = number of created games
NUM_WORKERS = 10
WORKER_LOAD = 50000
NUM_REPETITIONS = 1000

# complexGame = ComplexGame(num_workers, worker_load)
game = SimpleGame(NUM_WORKERS, WORKER_LOAD)
# game = ComplexGame(NUM_WORKERS, WORKER_LOAD)


start_time = time.time()
for i in range(1, NUM_REPETITIONS+1):

    print(i)
    games_rdd = game.create_quantum_games(sc).flatMap(lambda game_list: game_list)
    games_rdd.saveAsTextFile(file_path + str(i))

    current_time = time.time()
    ellapsed_time = current_time - start_time
    estimated_time_left = ((ellapsed_time / i) * (NUM_REPETITIONS - i)) / 3600
    print(estimated_time_left)

sc.stop()
