package ch.ethz.gametheory.ptesolver.json;

import ch.ethz.gametheory.ptesolver.GameFactory;
import ch.ethz.gametheory.ptesolver.GameWithImperfectInformation;
import ch.ethz.gametheory.ptesolver.NaiveGameFactory;
import ch.ethz.gametheory.ptesolver.PTESolver;
import org.json.JSONObject;

public class JsonSolver {

    public static String solveWithIntegers(final String json) throws IllegalAccessException {
        return solve(json, Integer.class);
    }

    public static String solveWithDoubles(final String json) throws IllegalAccessException {
        return solve(json, Double.class);
    }

    private static <T extends Comparable<T>> String solve(final String json, final Class<T> clazz) throws IllegalAccessException {
        JsonGame<T> jsonGame = new JsonGame<>(json, clazz);
        GameFactory<T> factory = new NaiveGameFactory<>(clazz);
        factory.parseData(jsonGame.getChoiceNodeToInformationSetMap(), jsonGame.getInformationSetToPlayerMap(), jsonGame.getPartialActions(), jsonGame.getOutcomes());
        GameWithImperfectInformation<T> game = factory.createGame();
        PTESolver<T> pteSolver = new PTESolver<>(game, clazz);
        T[][] solve = pteSolver.solve();
        return JSONObject.valueToString(solve);
    }


}
