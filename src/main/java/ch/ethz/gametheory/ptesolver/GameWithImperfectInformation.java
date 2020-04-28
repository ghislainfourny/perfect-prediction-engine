package ch.ethz.gametheory.ptesolver;

public class GameWithImperfectInformation {

    private DependencyStructure ds;
    private MaximinStructure ms;
    private OutcomeStructure os;
    private String[] playerNames;
    private String[] strategyNames;

    GameWithImperfectInformation(DependencyStructure ds,
                                 MaximinStructure ms,
                                 OutcomeStructure os,
                                 String[] playerNames,
                                 String[] strategyNames){
        this.ds = ds;
        this.ms = ms;
        this.os = os;
        this.playerNames = playerNames;
        this.strategyNames = strategyNames;

    }

    public DependencyStructure getDependencyStructure() {
        return ds;
    }

    public MaximinStructure getMaximinStructure() {
        return ms;
    }

    public OutcomeStructure getOutcomeStructure() {
        return os;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }

    public String[] getStrategyNames() {
        return strategyNames;
    }

}
