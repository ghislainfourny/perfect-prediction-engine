package ch.ethz.gametheory.ptesolver;

class ChoiceNode implements Node {
    private Edge[] actions; // (sorted) array of edges
    private int nOutcomes; // number of not eliminated outcomes

    public void setActions(Edge[] actions) {
        this.actions = actions;
    }

    public Edge[] getActions() {
        return actions;
    }

    public int initializeNumberOfOutcomes(){
        nOutcomes = 0;
        for (Edge e: actions) {
            nOutcomes += e.initializeNumberOfOutcomes();
        }
        return nOutcomes;
    }

    public int getNumberOfOutcomes(){
        return nOutcomes;
    }

    public int[][] getRemainingOutcomes() {
        int[][] result = null;
        if (nOutcomes != 0) {
            int[][] out = new int[nOutcomes][];
            int index = 0;
            // check for all edges if there are remaining outcomes; if there are, get all of them
            for (Edge e : actions) {
                int n = e.getNumberOfOutcomes();
                if (n > 0)
                    for (int[] i : e.getRemainingOutcomes()) {
                        out[index++] = i;
                    }
            }
            result = out;
        }
        return result;
    }

    public int eliminateOutcomes(int[] maximinValues){
        if (nOutcomes==0) return 0; 
        int eliminated = 0;
        for (Edge e: actions) eliminated += e.eliminateOutcomes(maximinValues);
        nOutcomes -= eliminated;
        return eliminated;
    }

    public Integer[] getMin(int playerNum) {
        Integer[] min = new Integer[actions.length];
        for (int i = 0; i < actions.length; i++){
            min[i] = actions[i].getMin(playerNum);
        }
        return min;
    }

    public boolean isParetoOptimal(int[] values){
        boolean isParetoOptimal = true;
        for (Edge e : actions){
            isParetoOptimal &= e.isParetoOptimal(values);
        }
        return isParetoOptimal;
    }

}