import os
import time
from datetime import datetime
from pyspark.sql import SparkSession
from SimpleGame import SimpleGame
from ComplexGame import ComplexGame

spark = SparkSession.builder.master("local[*]").appName("GameCreator").getOrCreate()    
sc = spark.sparkContext

current_datetime = datetime.now().strftime("%Y%m%d%H%M%S")

# num_workers * worker_load = number of created games
OUTPUT_PATH = "/tmp/generated/games"
NUM_WORKERS = 80
WORKER_LOAD = 50000
NUM_REPETITIONS = 1

# game = SimpleGame(NUM_WORKERS, WORKER_LOAD)
game = ComplexGame(NUM_WORKERS, WORKER_LOAD)


start_time = time.time()
for i in range(1, NUM_REPETITIONS+1):

    print(i)
    games_rdd = game.create_quantum_games(sc).flatMap(lambda game_list: game_list)
    games_rdd.saveAsTextFile(OUTPUT_PATH + str(i))

    current_time = time.time()
    ellapsed_time = current_time - start_time
    estimated_time_left = ((ellapsed_time / i) * (NUM_REPETITIONS - i)) / 3600
    print(estimated_time_left)

sc.stop()
