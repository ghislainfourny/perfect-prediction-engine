package ch.ethz.gametheory.ptesolver;

public class PTESolver<T extends Comparable<T>> {

    private final Class<T> clazz;
    private GameWithImperfectInformation<T> game; // current game
    private boolean solved; // true if game was solved
    private T[][] solution; // contains all solutions after solving

    public PTESolver(GameWithImperfectInformation<T> game, final Class<T> clazz) {
        this.clazz = clazz;
        setGame(game);
    }

    public void setGame(GameWithImperfectInformation<T> game) {
        this.game = game;
        this.solution = null;
        this.solved = false;
    }

    /**
     * @return all outcomes which conform to the PTE; empty array if there are none
     */
    public T[][] solve() {
        DependencyStructure ds = game.getDependencyStructure();
        MaximinStructure<T> ms = game.getMaximinStructure();
        OutcomeStructure<T> es = game.getOutcomeStructure();

        boolean[] reached = ds.getReachedInformationSets();
        T[] maximin = ms.getMaximin(reached);

        while (es.eliminateOutcomes(maximin)) {
            reached = ds.getReachedInformationSets();
            maximin = ms.getMaximin(reached);
        }

        T[][] result = es.getRemainingOutcomes();
        if (result == null) {
            result = GenericUtils.getMatrix(clazz, 0);
        }
        this.solution = result;
        this.solved = true;
        return solution;
    }


    /**
     * @return true if there is more is one or less outcomes left after solving
     * @throws IllegalAccessException if game was not solved before calling
     */
    public boolean isUnique() throws IllegalAccessException {
        if (solved) {
            return solution.length <= 1;
        } else {
            throw new IllegalAccessException("Need to run solve method first!");
        }

    }

    /**
     * @return true if all outcomes are pareto-optimal (also true if there are none)
     * @throws IllegalAccessException if game was not solved before calling
     */
    public boolean isParetoOptimal() throws IllegalAccessException {
        if (solved) {
            for (T[] i : solution) {
                if (!game.getOutcomeStructure().isParetoOptimal(i)) return false;
            }
            return true;
        } else {
            throw new IllegalAccessException("Need to run solve method first!");
        }
    }

    /**
     * @return true if there exists a solution
     * @throws IllegalAccessException if game was not solved before calling
     */
    public boolean hasSolution() throws IllegalAccessException {
        if (solved) {
            return solution.length > 0;
        } else {
            throw new IllegalAccessException("Need to run solve() method first!");
        }
    }

}
