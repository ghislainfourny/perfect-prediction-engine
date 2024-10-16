package ch.ethz.gametheory.ptesolver;

public interface Node<T extends Comparable<T>> {

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
    T[][] getRemainingOutcomes();

    /**
     * @param maximinValues expects one maximin value for each player; if a player didn't have an information set
     *                      activated this round the value should be Integer.MIN_VALUE
     * @return the amount of outcomes that were eliminated in this elimination step
     */
    int eliminateOutcomes(T[] maximinValues);

    /**
     * @param playerNum whose min value should be returned
     * @return index i is the min value of the i-th edge (sorted) of this node;
     * null if all edges are impossible (all children are eliminated)
     */
    T[] getMin(int playerNum);

    /**
     * @param values array with the length of numbers of players
     * @return true if none of the outcomes in this subtree pareto dominate values
     */
    boolean isParetoOptimal(T[] values);
    
    /**
     * @return the number of the only remaining action, -1 if zero or more than one.
     */
    public int getOnlyRemainingAction();
}
