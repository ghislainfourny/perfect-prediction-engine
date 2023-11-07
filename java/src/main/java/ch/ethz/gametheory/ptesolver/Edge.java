package ch.ethz.gametheory.ptesolver;

import org.apache.commons.lang3.ObjectUtils;

class Edge<T extends Comparable<T>> {

    private final int actionNum; // action identifier of this edge
    private final Node<T> to; // child node
    private int nOutcomes; // number of not eliminated outcomes in this subtree

    public Edge(int actionNum, Node<T> to) {
        this.actionNum = actionNum;
        this.to = to;
    }

    /**
     * @return action identifier of this edge; not necessary but useful for debugging purposes
     */
    public int getActionNum() {
        return actionNum;
    }

    /**
     * @param maximinValues self-explanatory
     * @return number of deleted outcomes in this step in this subtree
     */
    public int eliminateOutcomes(T[] maximinValues) {
        if (nOutcomes == 0) return 0; // preemptively returns 0 if there are no outcomes left in subtree
        int deleted = to.eliminateOutcomes(maximinValues);
        nOutcomes -= deleted;
        return deleted;
    }

    /**
     * @return number of outcomes in this subtree
     */
    public int initializeNumberOfOutcomes() {
        nOutcomes = to.initializeNumberOfOutcomes();
        return nOutcomes;
    }

    /**
     * @return getter function for number of not eliminated outcomes
     */
    public int getNumberOfOutcomes() {
        return nOutcomes;
    }

    /**
     * @return if an outcome was not eliminated via eliminateOutcomes it will return here; if there's none, returns null
     */
    public T[][] getRemainingOutcomes() {
        if (nOutcomes > 0) return to.getRemainingOutcomes();
        return null;
    }

    /**
     * @param playerNum whose min value should be returned
     * @return min of player playerNum of child node; null if child node is impossible
     */
    public T getMin(int playerNum) {
        if (nOutcomes == 0) return null;
        T min = null;
        T[] minValues = to.getMin(playerNum);
        for (T val : minValues) {
            if (val != null) {
                min = ObjectUtils.min(val, min);
            }
        }
        return min;
    }

    /**
     * @param values array with the length of numbers of players
     * @return true if none of the outcomes in this subtree pareto dominate values
     */
    public boolean isParetoOptimal(T[] values) {
        return to.isParetoOptimal(values);
    }
}
