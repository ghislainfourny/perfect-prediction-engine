from datetime import datetime
from pyspark import SparkConf, SparkContext
from abc import ABC, abstractmethod
import utils

class Game(ABC):
    def __init__(self, num_workers, worker_load):
        self.precision = int(1e5)
        self.num_workers = num_workers
        self.worker_load = worker_load


    @abstractmethod
    def create_game(self, payoffs, leaves):
        pass


    def create_quantum_games(self, sc):
        """
        Creates given number of quantum games 
        """
        machines_rdd = sc.parallelize(range(self.num_workers), self.num_workers)
        number_range = utils.get_number_list(self.precision)

        payoffs_rdd = machines_rdd.map(lambda _: self.payoffs_func(number_range))
        games_rdd = payoffs_rdd.map(lambda payoff: self.create_game(payoff, self.leaves))

        return games_rdd


    @abstractmethod
    def generate_quantum_payoff(self, number_range):
        pass

    def payoffs_func(self, number_range):
        payoffs = set()
        for _ in range(self.worker_load):
            payoff = self.generate_quantum_payoff(number_range)
            if payoff not in payoffs:
                payoffs.add(payoff)
        return list(payoffs)