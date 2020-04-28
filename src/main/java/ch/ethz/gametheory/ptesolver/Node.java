package ch.ethz.gametheory.ptesolver;

public interface Node {

    /**
     * @return used by the constructor to initialize how many outcomes one node has as children (or is itself)
     */
    int initializeNumberOfOutcomes();

    /**
     * @return the amount of not eliminated outcomes this node has as children (or is itself)
     */
    int getNumberOfOutcomes();

    /**
     * @return if an outcome was not eliminated via eliminateOutcomes it will return here; if there's none, returns null
     */
    int[][] getRemainingOutcomes();

    /**
     * @param maximinValues expects one maximin value for each player; if a player didn't have an informationset
     *                      activated this round the value should be Integer.MIN_VALUE
     * @return the amount of outcomes that were eliminated in this elimination step
     */
    int eliminateOutcomes(int[] maximinValues);

    /**
     * @param playerNum whose min value should be returned
     * @return index i is the min value of the i-th edge (sorted) of this node;
     * null if all edges are impossible (all children are eliminated)
     */
    Integer[] getMin(int playerNum);

    /**
     * @param values array with the length of numbers of players
     * @return true if none of the outcomes in this subtree pareto dominate values
     */
    boolean isParetoOptimal(int[] values);
}
