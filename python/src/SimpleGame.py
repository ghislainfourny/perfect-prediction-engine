import math
import random
from datetime import datetime
import os
import numpy as np
import utils
from Node import ChoiceNode, OutcomeNode
from Game import Game

class SimpleGame(Game):
    def __init__(self, num_workers, worker_load):
        super().__init__(num_workers, worker_load)
        self.leaves = 4
        self.players = 2


    def generate_quantum_payoff(self, number_range):
        player1_samples = random.sample(number_range, self.leaves)
        theta_gnerator = random.uniform(0,1.0)
        phi = math.radians(random.uniform(0,360.0))
        theta = math.acos(2*theta_gnerator - 1)

        cos_theta = math.cos(theta)
        sin_theta = math.sin(theta)
        cos_phi = math.cos(phi)
        payoff = (tuple(player1_samples), (-cos_theta, cos_theta, -sin_theta*cos_phi, sin_theta*cos_phi))

        return payoff


    def create_generic_games(self):
        """
        Creates all possible integer games for the given amount of players
        """
        current_datetime = datetime.now().strftime("%Y%m%d%H%M%S")
        file_path = os.environ.get("OUTPUT_GENERATED") + current_datetime + "/generic.json"

        outputs = utils.generate_outputs(self.leaves, self.players)
        with open(file_path, "w", encoding="utf-8") as file:
            for output in outputs:
                print(self.create_game(output, self.leaves), file=file)


    def create_random_games(self, number_of_games):
        """
        Creates number_of_games random games with double value payoffs for the given
        number of players
        """
        current_datetime = datetime.now().strftime("%Y%m%d%H%M%S")
        file_path = os.environ.get("OUTPUT_GENERATED") + current_datetime + "/random.json"

        outputs = utils.generate_random_outputs(number_of_games, self.leaves, self.players)
        with open(file_path, "w", encoding="utf-8") as file:
            for output in outputs:
                print(self.create_game(output, self.leaves), file=file)


    def create_game(self, payoffs, leaves):
        """
        Builds the games structure with nodes and payoffs
        """
        games = []
        for payoff in payoffs:
            A = ChoiceNode(1, 1)

            B1 = ChoiceNode(2, 2)
            B2 = ChoiceNode(2, 3)

            A.add_child(B1)
            A.add_child(B2)

            last_nodes = [B1, B2]

            array = np.array(payoff)

            for i in range(leaves//2):
                last_nodes[i].add_child(OutcomeNode(array[:, 2*i].tolist()))
                last_nodes[i].add_child(OutcomeNode(array[:, 2*i+1].tolist()))

            games.append(utils.convert_game_to_json(A))
        return games
