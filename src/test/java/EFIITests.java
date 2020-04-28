import ch.ethz.gametheory.ptesolver.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class EFIITests {
    int[] choiceNodeToInformationSetMap;
    int[] informationSetToPlayerMap;
    int[][] partialActions;
    int[][] outcomes;
    int[][] expectedResult;

    public EFIITests(int[] choiceNodeToInformationSetMap,
                   int[] informationSetToPlayerMap,
                   int[][] partialActions,
                   int[][] outcomes,
                   int[][] expectedResult){
        this.choiceNodeToInformationSetMap = choiceNodeToInformationSetMap;
        this.informationSetToPlayerMap = informationSetToPlayerMap;
        this.partialActions = partialActions;
        this.outcomes = outcomes;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters(name = "Run {index}: pdpToDpMap={0}, dpToPlayerMap={1}, partialAction={2}, outcomes={3}, expectedResult={4}")
    public static Iterable<Object[]> data() {

        return Arrays.asList(new Object[][]{
                { //0
                    new int[]{0,1,2,3,3,4,4,5,5,6,6,7},
                    new int[]{0,1,1,0,0,1,1,0},
                    new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{4,2,5},{5,2,6},{6,3,7},{7,3,12},{6,4,8},{7,4,13},
                            {10,5,9},{11,5,14},{10,6,10},{11,6,15},{8,7,16},{9,7,17},{8,8,18},{9,8,19},{12,9,20},
                            {13,9,11},{12,10,21},{13,10,22},{14,11,23},{15,11,24}},
                    new int[][]{{5,8},{8,12},{3,1},{9,9},{2,4},{7,2},{6,7},{4,3},{13,5},{12,13},{1,10},{11,6},{10,11}},
                    new int[][]{{12,13}}
                        },
                { //1
                    new int[]{0,1,2,3,4,4,3},
                        new int[]{0,1,1,0,0},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{4,2,5},{5,2,6},{6,3,7},{7,3,8},{8,4,9},{9,4,10},
                                {8,5,11},{9,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{5,8},{8,12},{3,1},{9,9},{2,4},{7,2},{6,7},{4,3}},
                        new int[][]{{8,12}}},
                { //2
                    new int[]{0,1,2,3,4,4,3},
                        new int[]{0,1,1,0,0},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{4,2,5},{5,2,6},{6,3,7},{7,3,8},{8,4,9},{9,4,10},
                                {8,5,11},{9,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{9,15},{5,10},{1,8},{14,2},{3,0},{12,13},{11,7},{4,6}},
                        new int[][]{{14,2}}},
                { //3
                    new int[]{0,1,2,3,4,4,3},
                        new int[]{0,1,1,0,0},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{4,2,5},{5,2,6},{6,3,7},{7,3,8},{8,4,9},{9,4,10},
                                {8,5,11},{9,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{0,7},{9,5},{10,12},{2,13},{3,11},{1,8},{6,15},{4,14}},
                        new int[][]{{10,12}}},
                { //4
                    new int[]{0,1,2,3,3,3,3},
                        new int[]{0,1,1,0},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{4,2,5},{5,2,6},{6,3,7},{7,3,8},{6,4,9},{7,4,10},
                                {6,5,11},{7,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{7,2},{6,7},{4,3},{13,5},{12,13},{1,10},{11,6},{10,11}},
                        new int[][]{{13,5}}
                },
                { //5
                    new int[]{0,1,2,3,3,3,3},
                        new int[]{0,1,1,0},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{4,2,5},{5,2,6},{6,3,7},{7,3,8},{6,4,9},{7,4,10},
                                {6,5,11},{7,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{-1,7},{0,15},{-2,9},{1,2},{13,4},{12,11},{6,3},{8,5}},
                        new int[][]{}
                },
                { //6
                    new int[]{0,1,2,3,3,3,3},
                        new int[]{0,1,1,0},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{4,2,5},{5,2,6},{6,3,7},{7,3,8},{6,4,9},{7,4,10},
                                {6,5,11},{7,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{10,6},{4,13},{15,11},{3,7},{14,8},{5,9},{1,0},{2,12}},
                        new int[][]{{15,11}}
                },
                { //7
                        new int[]{0,1,1,2,3,3,3},
                        new int[]{0,1,0,2},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{2,2,5},{3,2,6},{4,3,7},{5,3,8},{6,4,9},{7,4,10},
                                {6,5,11},{7,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{10,6,4},{4,13,2},{15,11,3},{3,7,6},{14,8,7},{5,9,1},{1,0,8},{2,12,5}},
                        new int[][]{{15,11,3}}
                },
                { //8
                        new int[]{0,1,1,2,3,3,3},
                        new int[]{0,1,0,2},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{2,2,5},{3,2,6},{4,3,7},{5,3,8},{6,4,9},{7,4,10},
                                {6,5,11},{7,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{0,7,7},{9,5,1},{10,12,6},{2,13,4},{3,11,8},{1,8,2},{6,15,5},{4,14,3}},
                        new int[][]{{10,12,6}}
                },
                { //9
                        new int[]{0,1,1,2,3,3,3},
                        new int[]{0,1,0,2},
                        new int[][]{{0,0,1},{1,0,2},{2,1,3},{3,1,4},{2,2,5},{3,2,6},{4,3,7},{5,3,8},{6,4,9},{7,4,10},
                                {6,5,11},{7,5,12},{6,6,13},{7,6,14}},
                        new int[][]{{9,15,5},{5,10,4},{1,8,1},{14,2,3},{3,0,2},{12,13,8},{11,7,7},{4,6,6}},
                        new int[][]{{12,13,8}}
                }
        });

    }

    @Test
    public void testEFGames() {
        GameFactory factory = new NaiveGameFactory();

        try {
            System.out.println("choiceNodeToInformationSetMap: " + Arrays.toString(choiceNodeToInformationSetMap));
            System.out.println("informationSetToPlayerMap: " + Arrays.toString(informationSetToPlayerMap));
            System.out.print("partialActions: ");
            for (int[] pa: partialActions) {
                System.out.print(Arrays.toString(pa) + " ");
            }
            System.out.print("\noutcomes: ");
            for (int[] o: outcomes) {
                System.out.print(Arrays.toString(o) + " ");
            }

            factory.parseData(choiceNodeToInformationSetMap, informationSetToPlayerMap, partialActions,outcomes);
            GameWithImperfectInformation game = factory.createGame();
            PTESolver solver = new PTESolver(game);
            int[][] result = solver.solve();
            assertArrayEquals(expectedResult, result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
