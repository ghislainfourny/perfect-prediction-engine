package ch.ethz.gametheory.ptesolver;

public interface OutcomeStructure {

    /**
     * @param maximinValues expects one maximin value for each player; if a player didn't have an informationset
     *                      activated this round the value should be Integer.MIN_VALUE
     * @return true if at least one value was eliminated
     */
    boolean eliminateOutcomes(int[] maximinValues);

    /**
     * @return if a outcome was not eliminated via eliminateOutcomes it will return here; if there's none, returns null
     */
    int[][] getRemainingOutcomes();

    /**
     * @param values array with the length of numbers of players
     * @return true if none of the outcomes in this game pareto dominate this value
     */
    boolean isParetoOptimal(int[] values);
}
