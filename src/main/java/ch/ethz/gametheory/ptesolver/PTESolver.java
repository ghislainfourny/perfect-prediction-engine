package ch.ethz.gametheory.ptesolver;

import java.util.Arrays;

public class PTESolver {

    private GameWithImperfectInformation game; // current game
    private int[][] solution; // contains all solutions after solving
    private boolean solved; // true if game was solved

    public PTESolver(GameWithImperfectInformation game) {
        setGame(game);
    }

    public void setGame(GameWithImperfectInformation game) {
        this.game = game;
        this.solution = null;
        this.solved = false;
    }

    /**
     * @return all outcomes which conform to the PTE; empty array if there are none
     */
    public int[][] solve() {
        DependencyStructure ds = game.getDependencyStructure();
        MaximinStructure ms = game.getMaximinStructure();
        OutcomeStructure es = game.getOutcomeStructure();

        boolean[] reached = ds.getReachedInformationSets();
        int[] maximin = ms.getMaximin(reached);

        while (es.eliminateOutcomes(maximin)){
            reached = ds.getReachedInformationSets();
            maximin = ms.getMaximin(reached);
        }

        int [][] result = es.getRemainingOutcomes();
        if (result == null)
            result = new int[0][0];
        this.solution = result;
        this.solved = true;
        return solution;
    }

    /**
     * @return true if there is more is one or less outcomes left after solving
     * @throws IllegalAccessException if game was not solved before calling
     */
    public boolean isUnique() throws IllegalAccessException {
        if (solved){
            return solution.length<=1;
        }else {
            throw new IllegalAccessException("Need to run solve method first!");
        }

    }

    /**
     * @return true if all outcomes are pareto-optimal (also true if there are none)
     * @throws IllegalAccessException if game was not solved before calling
     */
    public boolean isParetoOptimal() throws IllegalAccessException {
        if (solved) {
            for (int[] i : solution) {
                if (!game.getOutcomeStructure().isParetoOptimal(i)) return false;
            }
            return true;
        }else {
            throw new IllegalAccessException("Need to run solve method first!");
        }
    }

    /**
     * @return true if there exists a solution
     * @throws IllegalAccessException if game was not solved before calling
     */
    public boolean hasSolution() throws IllegalAccessException {
        if (solved){
            return solution.length>0;
        }else {
            throw new IllegalAccessException("Need to run solve() method first!");
        }
    }

}
