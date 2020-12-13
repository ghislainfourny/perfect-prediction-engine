import ch.ethz.gametheory.ptesolver.GameFactory;
import ch.ethz.gametheory.ptesolver.GameWithImperfectInformation;
import ch.ethz.gametheory.ptesolver.NaiveGameFactory;
import ch.ethz.gametheory.ptesolver.PTESolver;
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
public class EFTests {


    int[] pdpTOdpMap;
    int[] dpToPlayerMap;
    int[][] partialActions;
    Integer[][] outcomes;
    Integer[][] expectedResult;

    public EFTests(int[] pdpTOdpMap,
                   int[] dpToPlayerMap,
                   int[][] partialActions,
                   Integer[][] outcomes,
                   Integer[][] expectedResult) {
        this.pdpTOdpMap = pdpTOdpMap;
        this.dpToPlayerMap = dpToPlayerMap;
        this.partialActions = partialActions;
        this.outcomes = outcomes;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters(name = "Run {index}: pdpToDpMap={0}, dpToPlayerMap={1}, partialAction={2}, outcomes={3}, expectedResult={4}")
    public static Iterable<Object[]> data() throws Throwable {

        File file = new File("src/test/resources/eftestdata.txt");
        Scanner scanner = new Scanner(file);
        Queue<Object[]> tests = new LinkedList<>();

        while (scanner.hasNextLine()){
            String temp = scanner.nextLine();
            Object[] elem = getTestData(temp);
            tests.add(elem);

        }

        return tests;
    }

    private static Object[] getTestData(String data){

        /*
         * y has the game as a tree structure
         *    p is the player
         *    c the choices
         * P contains the PTE equilibrium as the list of choices to go down the tree
         */

        Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(data);
        JsonElement json_P = object.get("P");
        JsonElement json_y = object.get("y");

        int[] java_P = gson.fromJson(json_P, int[][].class)[0];
        Map java_y = gson.fromJson(json_y, Map.class);

        Queue<Integer> pdpToDpMap = new LinkedList();
        Queue<Integer> dpToPlayerMap = new LinkedList();
        Queue<Integer[]> partialAction = new LinkedList();
        Queue<Integer[]> partialActionsForOutcomes = new LinkedList();
        Queue<Integer[]> outcomes = new LinkedList();

        Queue<Map> bfs = new LinkedList();
        Queue<Integer> pdpNum = new LinkedList();
        bfs.add(java_y);
        pdpNum.add(0);
        int pdpCounter = 1;
        int paCounter = 0;
        while (!bfs.isEmpty()){
            Map temp = bfs.poll();
            int tempPdpNum = pdpNum.poll();
            dpToPlayerMap.add(((int) Math.round((double)temp.get("p"))));
            pdpToDpMap.add(tempPdpNum);
            ArrayList<?> edges = (ArrayList) temp.get("c");
            int listLen = edges.size();
            for (int i = 0; i < listLen; i++) {

                Object tempNode2 = edges.get(i);
                if (tempNode2 instanceof Map){
                    Map node = (Map) tempNode2;
                    partialAction.add(new Integer[]{paCounter++, tempPdpNum, pdpCounter});
                    bfs.add(node);
                    pdpNum.add(pdpCounter++);
                } else {
                    ArrayList<Double> list = (ArrayList) tempNode2;
                    Integer[] outcome = new Integer[list.size()];
                    for (int j = 0; j < outcome.length; j++)
                        outcome[j] = (int) Math.round(list.get(j));
                    partialActionsForOutcomes.add(new Integer[]{paCounter++, tempPdpNum});
                    outcomes.add(outcome);
                }
            }
        }

        while(!partialActionsForOutcomes.isEmpty()){
            Integer[] temp = partialActionsForOutcomes.poll();
            partialAction.add(new Integer[]{temp[0], temp[1], pdpCounter++});
        }

        int[] pdpToDp = new int[pdpToDpMap.size()];
        int[] dpToPlayer = new int[dpToPlayerMap.size()];
        int[][] pa = new int[partialAction.size()][3];
        Integer[][] out = new Integer[outcomes.size()][2];

        for (int i = 0; i < pdpToDp.length; i++)
            pdpToDp[i] = pdpToDpMap.poll();
        for (int i = 0; i < dpToPlayer.length; i++)
            dpToPlayer[i] = dpToPlayerMap.poll();
        for (int i = 0; i < pa.length; i++) {
            Integer[] temp = partialAction.poll();
            pa[i][0] = temp[0];
            pa[i][1] = temp[1];
            pa[i][2] = temp[2];
        }
        for (int i = 0; i < out.length; i++) {
            Integer[] temp = outcomes.poll();
            out[i][0] = temp[0];
            out[i][1] = temp[1];
        }

        // TODO this does not work for other trees
        int currentNode = 0;
        int actionPos = 0;
        for (int value : java_P) {
            int c = 0;
            while (pa[actionPos][1] != currentNode || c++ != value) {
                actionPos++;
            }
            currentNode = pa[actionPos][2];
        }

        Integer[][] exp = {out[currentNode - pdpToDp.length]};

        return new Object[]{pdpToDp, dpToPlayer, pa, out, exp};

    }



    @Test
    public void testEFGames() {
        GameFactory<Integer> factory = new NaiveGameFactory<>(Integer.class);

        try {
            factory.parseData(pdpTOdpMap, dpToPlayerMap, partialActions, outcomes);
            GameWithImperfectInformation<Integer> game = factory.createGame();
            PTESolver<Integer> solver = new PTESolver<>(game, Integer.class);
            Integer[][] result = solver.solve();
            assertArrayEquals(expectedResult, result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
