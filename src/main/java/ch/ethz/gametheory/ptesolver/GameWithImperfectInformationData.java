package ch.ethz.gametheory.ptesolver;

public interface GameWithImperfectInformationData {

    int[] getChoiceNodeToInformationSetMap();

    int[] getInformationSetToPlayerMap();

    int[][] getPartialActions();

    Integer[][] getOutcomes();

    String[] getPlayerNames();

    String[] getActionNames();

}
