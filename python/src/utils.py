from itertools import permutations, product
import json
import random


def generate_permutations(length):
    """
    Returns all permutations of an integer list [0 .. length-1]
    """
    base_list = list(range(length))
    permuation_list = list(permutations(base_list))
    return permuation_list


def generate_outputs(leaves, players):
    """
    Generates the outputs for the integer games by building the product of the permutations
    """
    permuation_list = generate_permutations(leaves)
    outputs_list = list(product(permuation_list, repeat=players))
    return outputs_list


def generate_random_outputs(number, leaves, players):
    """
    Generates the outputs for the random games
    The unique_set ensures the uniqueness of every generated output tuple
    """
    numbers_range = get_number_list(1000)

    output_list = []
    unique_set = set()
    while len(output_list) < number:
        samples_list = random.sample(numbers_range, leaves*players)
        unique_numbers = split_in_half(samples_list)
        if unique_numbers not in unique_set:
            output_list.append(unique_numbers)
            unique_set.add(unique_numbers)
    return output_list


def get_number_list(length):
    """
    Returns a list of double numbers of length length
    """
    return [i/length for i in range(1, length+1)]


def convert_game_to_dict(root_node):
    """
    Converts a game to da dictionary that can later be converted to json
    """
    if not root_node.children:
        return root_node.attributes

    children_dict = {"children": [convert_game_to_dict(child)
                     for child in root_node.children]}

    return_dict = root_node.attributes
    return_dict.update(children_dict)
    return return_dict


def convert_game_to_json(root_node):
    """
    First converts a game to a dict and this dict to json
    """
    game_dict = convert_game_to_dict(root_node)
    return json.dumps(game_dict, ensure_ascii=False)


def split_in_half(list_to_split):
    """
    Takes a list, splits it in half and builds tuple of two tuples
    The two tuples each represent one half of the input list
    """
    assert len(list_to_split) % 2 == 0, "Cannot split list with uneven length"

    half_index = len(list_to_split) // 2

    first_half = tuple(list_to_split[:half_index])
    second_half = tuple(list_to_split[half_index:])

    return (first_half, second_half)
