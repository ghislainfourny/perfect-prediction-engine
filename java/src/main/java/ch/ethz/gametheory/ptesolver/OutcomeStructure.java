package ch.ethz.gametheory.ptesolver;

public interface OutcomeStructure<T extends Comparable<T>> {

    /**
     * @param maximinValues expects one maximin value for each player; if a player didn't have an information set
     *                      activated this round the value should be Integer.MIN_VALUE
     * @return true if at least one value was eliminated
     */
    boolean eliminateOutcomes(T[] maximinValues);

    /**
     * @return if a outcome was not eliminated via eliminateOutcomes it will return here; if there's none, returns null
     */
    T[][] getRemainingOutcomes();

    /**
     * @param values array with the length of numbers of players
     * @return true if none of the outcomes in this game pareto dominate this value
     */
    boolean isParetoOptimal(T[] values);
}
