from enum import Enum


class NodeType(Enum):
    CHOICE = 1
    OUTCOME = 2


class Node:
    def __init__(self, node_type, attributes):
        self.node_type = node_type
        self.attributes = attributes
        self.children = []

    def add_child(self, child):
        self.children.append(child)


class ChoiceNode(Node):
    def __init__(self, player_number, information_set):
        super().__init__(NodeType.CHOICE, {
            "kind": "choice", "player": player_number, "information-set": information_set})


class OutcomeNode(Node):
    def __init__(self, payoffs):
        super().__init__(NodeType.OUTCOME, {
            "kind": "outcome", "payoffs": payoffs})
