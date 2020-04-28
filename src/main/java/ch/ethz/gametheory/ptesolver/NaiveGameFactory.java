package ch.ethz.gametheory.ptesolver;

import java.util.LinkedList;

public class NaiveGameFactory extends GameFactory {

    private LinkedList<ChoiceNode>[] informationSets;

    public GameWithImperfectInformation createGame() throws IllegalAccessException {
        if (informationSetToPlayerMap == null)
            throw new IllegalAccessException("You need to parse the data before calling this method!");
        Node[] nodes = createNodes();
        createConnections(nodes);

        Node root = nodes[0];
        GameTree tree = new GameTree(root, informationSets, informationSetToPlayerMap, outcomes[0].length);
        return new GameWithImperfectInformation(tree, tree, tree, playerNames,actionNames);
    }

    /**
     * @return all nodes corresponding to the data
     */
    private Node[] createNodes(){
        informationSets = new LinkedList[informationSetToPlayerMap.length];
        for (int i = 0; i < informationSetToPlayerMap.length; i++) {
            informationSets[i] = new LinkedList<>();
        }
        Node[] nodes = new Node[choiceNodeToInformationSetMap.length + outcomes.length];
        for (int i = 0; i < choiceNodeToInformationSetMap.length; i++){
            ChoiceNode temp = new ChoiceNode();
            nodes[i] = temp;
            informationSets[choiceNodeToInformationSetMap[i]].add(temp);
        }
        for (int i = choiceNodeToInformationSetMap.length; i < nodes.length; i++) {
            nodes[i] = new Outcome(outcomes[i- choiceNodeToInformationSetMap.length]);
        }
        return nodes;
    }

    /**
     * adds the connections between the nodes
     * @param nodes all nodes
     */
    private void createConnections(Node[] nodes){
        LinkedList<Edge>[] choiceNodeEdges = new LinkedList[choiceNodeToInformationSetMap.length];
        for (int i = 0; i < choiceNodeEdges.length; i++){
            choiceNodeEdges[i] = new LinkedList<>();
        }
        for (int[] partialAction : partialActions) {
            Edge e = new Edge(partialAction[PA_ACTION_NUM_INDEX], nodes[partialAction[PA_TO_INDEX]]);
            choiceNodeEdges[partialAction[PA_FROM_INDEX]].add(e);
        }
        for (int i = 0; i < choiceNodeEdges.length; i++) {
            Edge[] e = new Edge[choiceNodeEdges[i].size()];
            e = choiceNodeEdges[i].toArray(e);
            ((ChoiceNode) nodes[i]).setActions(e);
        }
    }


}
