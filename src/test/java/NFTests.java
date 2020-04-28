import ch.ethz.gametheory.ptesolver.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class NFTests {


    int[] pdpTOdpMap;
    int[] dpTOplayerMap;
    int[][] partialActions;
    int[][] outcomes;
    int[][] expectedResult;

    public NFTests(int[] pdpTOdpMap,
                   int[] dpTOplayerMap,
                   int[][] partialActions,
                   int[][] outcomes,
                   int[][] expectedResult){
        this.pdpTOdpMap = pdpTOdpMap;
        this.dpTOplayerMap = dpTOplayerMap;
        this.partialActions = partialActions;
        this.outcomes = outcomes;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters(name = "Run {index}: pdpToDpMap={0}, dpToPlayerMap={1}, partialAction={2}, outcomes={3}, expectedResult={4}")
    public static Iterable<Object[]> data() throws Throwable {

        File file = new File("src/test/resources/nftestdata.txt");
        Scanner scanner = new Scanner(file);
        Queue<Object[]> tests = new LinkedList();

        while (scanner.hasNextLine()){
            String temp = scanner.nextLine();
            Object[] elem = getTestData(temp);
            tests.add(elem);
        }

        return tests;
    }

    private static Object[] getTestData(String data) {

        /*
         * y has the game as a 3x3 matrix of pairs of outcomes,
         * z the number of players,
         * P the PTE equilibrium which are the *matrix coordinates* (row then column) on y (not the payoffs).
         */


        Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(data);
        JsonElement json_z = object.get("z");
        JsonElement json_P = object.get("P");
        JsonElement json_y = object.get("y");

        int java_z = gson.fromJson(json_z, int.class);
        int[][] java_p = gson.fromJson(json_P, int[][].class);
        int[][][] java_y = gson.fromJson(json_y, int[][][].class);

        int[][] expectedResult = new int[java_p.length][java_z];
        for (int i = 0; i < java_p.length; i++) {
            expectedResult[i] = java_y[java_p[i][0]][java_p[i][1]];
        }

        int[] pdpToDpMap = {0,1,1,1};
        int[] dpToPlayerMap = {0,1};
        int[][] partialActions = new int[12][3];
        int[][] outcomes = new int[9][2];
        partialActions[0][0] = 0;
        partialActions[1][0] = 1;
        partialActions[2][0] = 2;
        partialActions[0][1] = 0;
        partialActions[1][1] = 0;
        partialActions[2][1] = 0;
        partialActions[0][2] = 1;
        partialActions[1][2] = 2;
        partialActions[2][2] = 3;

        for (int i = 0; i < java_y.length; i++) {
            for (int j = 0; j < java_y[i].length; j++) {
                int index = i*java_y.length+j;
                outcomes[index] = java_y[i][j];
                partialActions[index+3][0] = j+3;
                partialActions[index+3][1] = i+1;
                partialActions[index+3][2] = index+4;
            }
        }

        return new Object[] {pdpToDpMap, dpToPlayerMap, partialActions, outcomes, expectedResult};
    }

    @Test
    public void testNFGames() {
        GameFactory factory = new NaiveGameFactory();

        try {
            factory.parseData(pdpTOdpMap, dpTOplayerMap, partialActions,outcomes);
            GameWithImperfectInformation game = factory.createGame();
            PTESolver solver = new PTESolver(game);
            int[][] result = solver.solve();
            assertArrayEquals(expectedResult, result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
