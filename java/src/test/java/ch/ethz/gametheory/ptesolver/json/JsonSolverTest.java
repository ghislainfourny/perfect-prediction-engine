package ch.ethz.gametheory.ptesolver.json;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class JsonSolverTest {
    @Test
    public void prisonersDilemma() throws FileNotFoundException {
        File file = new File("src/test/resources/json/prisonersdilemma.json");
        try {
            String json = new String(Files.readAllBytes(file.toPath()));
            Integer[][] solve = JsonSolver.solveWithIntegers(json).getOutcome();
            System.out.println(solve);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void gameFormat() {
        File file = new File("src/test/resources/json/game-format.json");
        try {
            String json = new String(Files.readAllBytes(file.toPath()));
            Double[][] solve = JsonSolver.solveWithDoubles(json).getOutcome();
            System.out.println(solve);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
