package ch.ethz.gametheory.ptesolver;

import java.util.LinkedList;

public class NaiveGameFactory<T extends Comparable<T>> extends GameFactory<T> {

    private final Class<T> clazz;


    private LinkedList<ChoiceNode<T>>[] informationSets;

    public NaiveGameFactory(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public GameWithImperfectInformation<T> createGame() throws IllegalAccessException {
        if (informationSetToPlayerMap == null) {
            throw new IllegalAccessException("You need to parse the data before calling this method!");
        }
        Node<T>[] nodes = createNodes();
        createConnections(nodes);

        Node<T> root = nodes[0];
        GameTree<T> tree = new GameTree<>(clazz, root, informationSets, informationSetToPlayerMap, outcomes[0].length);
        return new GameWithImperfectInformation<>(tree, tree, tree, playerNames, actionNames);
    }

    /**
     * @return all nodes corresponding to the data
     */
    private Node<T>[] createNodes() {
        informationSets = new LinkedList[informationSetToPlayerMap.length];
        for (int i = 0; i < informationSetToPlayerMap.length; i++) {
            informationSets[i] = new LinkedList<>();
        }
        Node<T>[] nodes = new Node[choiceNodeToInformationSetMap.length + outcomes.length];
        for (int i = 0; i < choiceNodeToInformationSetMap.length; i++) {
            ChoiceNode<T> temp = new ChoiceNode<>(clazz);
            nodes[i] = temp;
            informationSets[choiceNodeToInformationSetMap[i]].add(temp);
        }
        for (int i = choiceNodeToInformationSetMap.length; i < nodes.length; i++) {
            nodes[i] = new Outcome(clazz, outcomes[i - choiceNodeToInformationSetMap.length]);
        }
        return nodes;
    }

    /**
     * adds the connections between the nodes
     *
     * @param nodes all nodes
     */
    private void createConnections(Node<T>[] nodes) {
        LinkedList<Edge<T>>[] choiceNodeEdges = new LinkedList[choiceNodeToInformationSetMap.length];
        for (int i = 0; i < choiceNodeEdges.length; i++) {
            choiceNodeEdges[i] = new LinkedList<>();
        }
        for (int[] partialAction : partialActions) {
            Edge<T> e = new Edge(partialAction[PA_ACTION_NUM_INDEX], nodes[partialAction[PA_TO_INDEX]]);
            choiceNodeEdges[partialAction[PA_FROM_INDEX]].add(e);
        }
        for (int i = 0; i < choiceNodeEdges.length; i++) {
            Edge<T>[] e = new Edge[choiceNodeEdges[i].size()];
            e = choiceNodeEdges[i].toArray(e);
            ((ChoiceNode<T>) nodes[i]).setActions(e);
        }
    }


}
