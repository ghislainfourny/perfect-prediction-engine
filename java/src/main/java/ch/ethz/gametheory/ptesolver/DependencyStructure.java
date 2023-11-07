package ch.ethz.gametheory.ptesolver;

public interface DependencyStructure {
    /**
     * @return index i is true if informationset i is activated
     */
    boolean[] getReachedInformationSets();
}
