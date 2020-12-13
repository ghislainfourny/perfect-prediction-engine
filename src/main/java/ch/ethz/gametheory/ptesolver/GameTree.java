package ch.ethz.gametheory.ptesolver;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.LinkedList;

public class GameTree<T extends Comparable<T>> implements MaximinStructure<T>, DependencyStructure, OutcomeStructure<T> {

    private final Class<T> clazz;
    private final Node<T> root;
    private final LinkedList<ChoiceNode<T>>[] informationSet;
    private int nOutcomes; // number of not eliminated outcomes remaining
    private final int[] informationSetToPlayerMap;
    private final int nPlayers; // number of players; needed for getMaximin


    GameTree(Class<T> clazz, Node<T> root, LinkedList<ChoiceNode<T>>[] informationSets, int[] informationSetToPlayerMap, int nPlayers) {
        this.clazz = clazz;
        this.root = root;
        this.informationSet = informationSets;
        this.nOutcomes = this.root.initializeNumberOfOutcomes();
        this.informationSetToPlayerMap = informationSetToPlayerMap;
        this.nPlayers = nPlayers;
    }

    public boolean[] getReachedInformationSets() {
        boolean[] out = new boolean[informationSet.length];
        for (int i = 0; i < informationSet.length; i++) {
            // if an information set reaches all not eliminated outcomes, it is activated (assumes canonical form)
            int acc = 0;
            for (Node<T> n : informationSet[i]) {
                acc += n.getNumberOfOutcomes();
            }
            out[i] = acc == nOutcomes;
        }
        return out;
    }

    public boolean eliminateOutcomes(T[] maximinValues) {
        int nEliminations = root.eliminateOutcomes(maximinValues);
        nOutcomes -= nEliminations;
        return nEliminations > 0;
    }

    public T[][] getRemainingOutcomes() {
        if (nOutcomes == 0) return null;
        return root.getRemainingOutcomes();
    }

    public boolean isParetoOptimal(T[] values) {
        return root.isParetoOptimal(values);
    }

    public T[] getMaximin(boolean[] reachedInformationSets) {
        T[] maximinValues = GenericUtils.getGenericArray(clazz, nPlayers); // index i is the maximin value of player i
        Arrays.fill(maximinValues, Integer.MIN_VALUE);
        for (int i = 0; i < reachedInformationSets.length; i++) {
            // if a information set is activated, compute maximin of this information set
            if (reachedInformationSets[i] && !informationSet[i].isEmpty()) {
                // index i of min is the min value of the action i
                // (assumes all nodes in an information set have same amount of edges and in order)
                T[] min = GenericUtils.getGenericArray(clazz, informationSet[i].getFirst().getActions().length);
                int playerNum = informationSetToPlayerMap[i];
                // go through all nodes in a information set and compute min for all actions
                for (ChoiceNode<T> n : informationSet[i]) {
                    T[] node_min = n.getMin(playerNum);
                    for (int j = 0; j < min.length; j++) {
                        if (node_min[j] != null &&
                                (min[j] == null || node_min[j].compareTo(min[j]) < 0)) {
                            min[j] = node_min[j];
                        }
                    }
                }
                // get max value of all the min values
                // since getMin can return null if a (partial) action is impossible and we don't
                // want to consider those values, we need to filter them first
                for (T value : min)
                    if (value != null) {
                        maximinValues[playerNum] = ObjectUtils.max(maximinValues[playerNum], value);
                    }
            }
        }
        return maximinValues;
    }

}
