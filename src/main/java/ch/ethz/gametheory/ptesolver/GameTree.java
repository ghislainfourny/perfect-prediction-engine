package ch.ethz.gametheory.ptesolver;

import java.util.*;

public class GameTree implements MaximinStructure, DependencyStructure, OutcomeStructure {

    private final Node root;
    private final LinkedList<ChoiceNode>[] informationSet;
    private int nOutcomes; // number of not eliminated outcomes remaining
    private final int[] informationSetToPlayerMap;
    private final int nPlayers; // number of players; needed for getMaximin


    GameTree(Node root, LinkedList<ChoiceNode>[] informationSets, int[] informationSetToPlayerMap, int nPlayers){
        this.root = root;
        this.informationSet = informationSets;
        this.nOutcomes = this.root.initializeNumberOfOutcomes();
        this.informationSetToPlayerMap = informationSetToPlayerMap;
        this.nPlayers = nPlayers;
    }

    public boolean[] getReachedInformationSets() {
        boolean[] out = new boolean[informationSet.length];
        for (int i = 0; i < informationSet.length; i++) {
            // if an informationset reaches all not eliminated outcomes, it is activated (assumes canonical form)
            int acc = 0;
            for (Node n: informationSet[i]) acc += n.getNumberOfOutcomes();
            out[i] = acc == nOutcomes;
        }
        return out;
    }

    public boolean eliminateOutcomes(int[] maximinValues) {
        int nEliminations = root.eliminateOutcomes(maximinValues);
        nOutcomes -= nEliminations;
        return nEliminations>0;
    }

    public int[][] getRemainingOutcomes() {
        if (nOutcomes == 0) return null;
        return root.getRemainingOutcomes();
    }

    public boolean isParetoOptimal(int[] values) {
        return root.isParetoOptimal(values);
    }

    public int[] getMaximin(boolean[] reachedInformationSets) {
        int[] maximinValues = new int[nPlayers]; // index i is the maximin value of player i
        Arrays.fill(maximinValues, Integer.MIN_VALUE);
        for (int i = 0; i < reachedInformationSets.length; i++) {
            // if a informationset is activated, compute maximin of this informationset
            if (reachedInformationSets[i] && !informationSet[i].isEmpty()){
                // index i of min is the min value of the action i
                // (assumes all nodes in an informationset have same amount of edges and in order)
                Integer[] min = new Integer[informationSet[i].getFirst().getActions().length];
                int playerNum = informationSetToPlayerMap[i];
                // go through all nodes in a informationset and compute min for all actions
                for (ChoiceNode n: informationSet[i]) {
                    Integer[] node_min = n.getMin(playerNum);
                    for (int j = 0; j < min.length; j++) {
                        if (node_min[j] != null &&
                                (min[j] == null || node_min[j] < min[j])  )
                                min[j] = node_min[j];
                    }
                }
                // get max value of all the min values
                // since getMin can return null if a (partial) action is impossible and we don't
                // want to consider those values, we need to filter them first
                for (Integer value : min)
                    if (value != null)
                        maximinValues[playerNum] = Integer.max(maximinValues[playerNum], value);
            }
        }
        return maximinValues;
    }

}
