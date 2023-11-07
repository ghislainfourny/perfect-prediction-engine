package ch.ethz.gametheory.ptesolver;

public class GameWithImperfectInformation<T extends Comparable<T>> {

    private final DependencyStructure ds;
    private final MaximinStructure<T> ms;
    private final OutcomeStructure<T> os;
    private final String[] playerNames;
    private final String[] strategyNames;

    GameWithImperfectInformation(DependencyStructure ds,
                                 MaximinStructure<T> ms,
                                 OutcomeStructure<T> os,
                                 String[] playerNames,
                                 String[] strategyNames) {
        this.ds = ds;
        this.ms = ms;
        this.os = os;
        this.playerNames = playerNames;
        this.strategyNames = strategyNames;

    }

    public DependencyStructure getDependencyStructure() {
        return ds;
    }

    public MaximinStructure<T> getMaximinStructure() {
        return ms;
    }

    public OutcomeStructure<T> getOutcomeStructure() {
        return os;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }

    public String[] getStrategyNames() {
        return strategyNames;
    }

}
