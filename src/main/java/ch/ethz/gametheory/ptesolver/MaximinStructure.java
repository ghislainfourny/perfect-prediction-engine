package ch.ethz.gametheory.ptesolver;

public interface MaximinStructure {
    /**
     * @param reachedInformationSets activated/reached informationsets
     * @return index i contains the maximin value of player i for the given activated informationsets;
     * if player i does not have activated informationset, returns Integer.MIN_VALUE
     */
    int[] getMaximin(boolean[] reachedInformationSets);
}
