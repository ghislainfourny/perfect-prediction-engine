package ch.ethz.gametheory.ptesolver;

class ChoiceNode<T extends Comparable<T>> implements Node<T> {

    private final Class<T> clazz;
    private Edge<T>[] actions; // (sorted) array of edges
    private int nOutcomes; // number of not eliminated outcomes

    ChoiceNode(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public Edge<T>[] getActions() {
        return actions;
    }

    public void setActions(Edge<T>[] actions) {
        this.actions = actions;
    }

    public int initializeNumberOfOutcomes() {
        nOutcomes = 0;
        for (Edge<T> e : actions) {
            nOutcomes += e.initializeNumberOfOutcomes();
        }
        return nOutcomes;
    }

    public int getNumberOfOutcomes() {
        return nOutcomes;
    }

    public T[][] getRemainingOutcomes() {
        T[][] result = null;
        if (nOutcomes != 0) {
            T[][] out = GenericUtils.getMatrix(clazz, nOutcomes);
            int index = 0;
            // check for all edges if there are remaining outcomes; if there are, get all of them
            for (Edge<T> e : actions) {
                int n = e.getNumberOfOutcomes();
                if (n > 0)
                    for (T[] i : e.getRemainingOutcomes()) {
                        out[index++] = i;
                    }
            }
            result = out;
        }
        return result;
    }

    public int eliminateOutcomes(T[] maximinValues) {
        if (nOutcomes == 0) return 0;
        int eliminated = 0;
        for (Edge<T> e : actions) {
            eliminated += e.eliminateOutcomes(maximinValues);
        }
        nOutcomes -= eliminated;
        return eliminated;
    }

    public T[] getMin(int playerNum) {
        T[] min = GenericUtils.getGenericArray(clazz, actions.length);
        for (int i = 0; i < actions.length; i++) {
            min[i] = actions[i].getMin(playerNum);
        }
        return min;
    }

    public boolean isParetoOptimal(T[] values) {
        boolean isParetoOptimal = true;
        for (Edge<T> e : actions) {
            isParetoOptimal &= e.isParetoOptimal(values);
        }
        return isParetoOptimal;
    }

}