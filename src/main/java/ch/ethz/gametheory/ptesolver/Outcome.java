package ch.ethz.gametheory.ptesolver;

public class Outcome implements Node {

    private int[] outcome;
    private int nOutcomes; // 1 if not eliminated, otherwise 0

    public Outcome(int[] outcome){
        this.outcome = outcome;
    }

    public int initializeNumberOfOutcomes(){
        nOutcomes = 1;
        return nOutcomes;
    }

    public int getNumberOfOutcomes() {
        return nOutcomes;
    }

    public int[][] getRemainingOutcomes() {
        int[][] out = new int[1][outcome.length];
        out[0] = outcome;
        return out;
    }

    public int eliminateOutcomes(int[] maximinValues) {
        if (nOutcomes==0) return 0;
        // if one of the maximin values is bigger than its value: eliminate outcome
        for (int i = 0; i < maximinValues.length; i++) {
            if (outcome[i]<maximinValues[i]) {
                nOutcomes = 0;
                break;
            }
        }
        return Math.abs(nOutcomes-1);
    }

    public Integer[] getMin(int playerNum) {
        return new Integer[]{outcome[playerNum]};
    }

    public boolean isParetoOptimal(int[] values) {
        boolean hasBetterOutcome = false;
        boolean isEqual = true;
        for (int i = 0; i < values.length; i++){
            hasBetterOutcome |= outcome[i] < values[i];
            isEqual &= outcome[i] == values[i];
        }
        return hasBetterOutcome||isEqual;
    }

}
