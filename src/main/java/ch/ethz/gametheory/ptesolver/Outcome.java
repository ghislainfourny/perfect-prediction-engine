package ch.ethz.gametheory.ptesolver;

public class Outcome<T extends Comparable<T>> implements Node<T> {

    private final Class<T> clazz;

    private final T[] outcome;
    private int nOutcomes; // 1 if not eliminated, otherwise 0

    public Outcome(final Class<T> clazz, T[] outcome) {
        this.clazz = clazz;
        this.outcome = outcome;
    }

    public int initializeNumberOfOutcomes() {
        nOutcomes = 1;
        return nOutcomes;
    }

    public int getNumberOfOutcomes() {
        return nOutcomes;
    }

    public T[][] getRemainingOutcomes() {
        T[][] ts = GenericUtils.getMatrix(clazz, 1);
        ts[0] = outcome;
        return ts;
    }

    public int eliminateOutcomes(T[] maximinValues) {
        if (nOutcomes == 0) return 0;
        // if one of the maximin values is bigger than its value: eliminate outcome
        for (int i = 0; i < maximinValues.length; i++) {
            if (maximinValues[i] != null && outcome[i].compareTo(maximinValues[i]) < 0) {
                nOutcomes = 0;
                break;
            }
        }
        return Math.abs(nOutcomes - 1);
    }

    public T[] getMin(int playerNum) {
        T[] min = GenericUtils.getGenericArray(clazz, 1);
        min[0] = outcome[playerNum];
        return min;
    }

    public boolean isParetoOptimal(T[] values) {
        boolean hasBetterOutcome = false;
        boolean isEqual = true;
        for (int i = 0; i < values.length; i++) {
            hasBetterOutcome |= outcome[i].compareTo(values[i]) > 0;
            isEqual &= outcome[i] == values[i];
        }
        return hasBetterOutcome || isEqual;
    }

}
