import math
import random
import numpy as np
import utils
from Node import ChoiceNode, OutcomeNode
from Game import Game

class ComplexGame(Game):
    def __init__(self, num_workers, worker_load):
        super().__init__(num_workers, worker_load)
        self.leaves = 16


    def generate_quantum_payoff(self, number_range):
        player1_samples = random.sample(number_range, self.leaves)
        player2_samples = random.sample(number_range, self.leaves)

        theta_generator = random.uniform(0, 1.0)
        phi = math.radians(random.uniform(0, 360.0))
        theta = math.acos(2*theta_generator - 1)

        cos_theta = math.cos(theta)
        sin_theta = math.sin(theta)
        cos_phi = math.cos(phi)

        cpst = cos_phi*sin_theta

        payoff = (
            tuple(player1_samples),
            tuple(player2_samples),
            (-cos_theta-1, -cos_theta+1, cos_theta+1, cos_theta-1, -cos_theta, -cos_theta,
                cos_theta, cos_theta, cpst, cpst, -cpst, -cpst, cpst-1, cpst+1, -cpst+1, -cpst-1),
            (cos_theta-1, -cos_theta+1, cos_theta+1, -cos_theta-1, -cpst, cpst, -cpst, cpst,
                cos_theta, -cos_theta, cos_theta, -cos_theta, -cpst-1, cpst+1, -cpst+1, cpst-1)
        )

        return payoff


    def create_game(self, payoffs, leaves):
        """
        Builds the game structure with nodes and payoffs
        """
        games = []
        for payoff in payoffs:
            A = ChoiceNode(1, 1)
            B1 = ChoiceNode(2, 2)
            B2 = ChoiceNode(2, 2)

            U1 = ChoiceNode(3, 3)
            U2 = ChoiceNode(3, 3)
            U3 = ChoiceNode(3, 4)
            U4 = ChoiceNode(3, 4)

            V1 = ChoiceNode(4, 5)
            V2 = ChoiceNode(4, 5)
            V3 = ChoiceNode(4, 6)
            V4 = ChoiceNode(4, 6)
            V5 = ChoiceNode(4, 5)
            V6 = ChoiceNode(4, 5)
            V7 = ChoiceNode(4, 6)
            V8 = ChoiceNode(4, 6)

            A.add_child(B1)
            A.add_child(B2)

            B1.add_child(U1)
            B1.add_child(U2)
            B2.add_child(U3)
            B2.add_child(U4)

            U1.add_child(V1)
            U1.add_child(V2)
            U2.add_child(V3)
            U2.add_child(V4)
            U3.add_child(V5)
            U3.add_child(V6)
            U4.add_child(V7)
            U4.add_child(V8)

            last_nodes = [V1, V2, V3, V4, V5, V6, V7, V8]

            array = np.array(payoff)


            for i in range(leaves//2):
                last_nodes[i].add_child(OutcomeNode(array[:, 2*i].tolist()))
                last_nodes[i].add_child(OutcomeNode(array[:, 2*i+1].tolist()))

            games.append(utils.convert_game_to_json(A))
        
        return games
