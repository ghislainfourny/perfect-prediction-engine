package ch.ethz.gametheory.ptesolver;

public interface GameWithImperfectInformationData<T extends Comparable<T>> {

    int[] getChoiceNodeToInformationSetMap();

    int[] getInformationSetToPlayerMap();

    int[][] getPartialActions();

    T[][] getOutcomes();

}
