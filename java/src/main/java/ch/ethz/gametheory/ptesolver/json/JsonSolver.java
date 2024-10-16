package ch.ethz.gametheory.ptesolver.json;

import ch.ethz.gametheory.ptesolver.GameFactory;
import ch.ethz.gametheory.ptesolver.GameWithImperfectInformation;
import ch.ethz.gametheory.ptesolver.NaiveGameFactory;
import ch.ethz.gametheory.ptesolver.PTESolver;
import ch.ethz.gametheory.ptesolver.Solution;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;

import java.io.File;

public class JsonSolver {

    public static Solution<Integer> solveWithIntegers(final String json) throws IllegalAccessException {
        return solve(json, Integer.class);
    }

    public static Solution<Double> solveWithDoubles(final String json) throws IllegalAccessException {
        return solve(json, Double.class);
    }

    private static <T extends Comparable<T>> Solution<T> solve(final String json, final Class<T> clazz) throws IllegalAccessException {
        JsonGame<T> jsonGame = new JsonGame<>(json, clazz);
        GameFactory<T> factory = new NaiveGameFactory<>(clazz);
        factory.parseData(jsonGame.getChoiceNodeToInformationSetMap(), jsonGame.getInformationSetToPlayerMap(), jsonGame.getPartialActions(), jsonGame.getOutcomes());
        GameWithImperfectInformation<T> game = factory.createGame();
        PTESolver<T> pteSolver = new PTESolver<>(game, clazz);
        Solution<T> solve = pteSolver.solve();
        return solve;
    }

    private static <T extends Comparable<T>> Set<Object[]> solveNash(JsonGame<T> jsonGame) throws IllegalAccessException {
        
        Set<Object[]> nashEqs = new HashSet<>();
        
        // getting the outcomes of the json game
        T[][] outcomes = jsonGame.getOutcomes();

        // generating all the strategies
        int[] bits = {0, 1};
        int length = 0;
        if(outcomes.length == 16){
            length = 6;
        }else if(outcomes.length == 4){
            length = 2;
        }
        
        List<List<Integer>> strategies = generateStrategies(bits, length);

        // iterating over all strategies
        for(int i=0; i<strategies.size(); i++) {
            List<Integer> strategy = strategies.get(i); // just a reference to the ith element

            List<Integer> validPath = validPath(new ArrayList<Integer>(strategy)); // passes a copy of strategy            

            assert validPath.size() != 0;


            T[] outcome = getOutcome(validPath, outcomes);

            boolean isNash = true;

            // get all deviations
            for(int j=0; j<length; j++) {
                int playerNumber = 0;
                if(j==0) {
                    playerNumber = 0;
                }
                else if(j==1) {
                    playerNumber = 1;
                }
                else if(j==2 || j==3) {
                    playerNumber = 2;
                }
                else {
                    playerNumber = 3;
                }
                T playerOutcome = outcome[playerNumber];
                List<Integer> devStrategy = new ArrayList<Integer>(strategy); // creats a copy of strategy
                Integer newVal = devStrategy.get(j) == 0 ? 1 : 0;
                devStrategy.set(j, newVal);
                List<Integer> devValidPath = validPath(devStrategy);

                T[] devOutcome = getOutcome(devValidPath, outcomes);
                T devPlayerOutcome = devOutcome[playerNumber];

                if(devPlayerOutcome.compareTo(playerOutcome) > 0) {
                    isNash = false;
                    break;
                }
            }

            if(isNash) {
                Object[] nashEq = {strategy, outcome};
                nashEqs.add(nashEq);
            }

        }

        return nashEqs;
    }

    private static List<List<Integer>> generateStrategies(int[] bits, int length) {
        List<List<Integer>> strategies = new ArrayList<>();
        generateStrategiesHelper(bits, length, new ArrayList<>(), strategies);
        return strategies;
    }

    private static void generateStrategiesHelper(int [] bits, int length, List<Integer> strategy, List<List<Integer>> strategies) {
        if(strategy.size() == length) {
            strategies.add(new ArrayList<>(strategy));
            return;
        }

        for(int i=0; i<bits.length; i++) {
            strategy.add(bits[i]);
            generateStrategiesHelper(bits, length, strategy, strategies);
            strategy.remove(strategy.size() -1);
        }
    }

    private static List<Integer> validPath(List<Integer> strategy) {

        List<Integer> validStrategy = new ArrayList<Integer>();

        if(strategy.size()==2) {
            return new ArrayList<Integer>(strategy);
        }
        
        validStrategy.add(strategy.get(0).intValue());
        validStrategy.add(strategy.get(1).intValue());
        
        if(strategy.get(0) == 0) {
            validStrategy.add(strategy.get(3).intValue());
        }
        else if(strategy.get(0) == 1) {
            validStrategy.add(strategy.get(2).intValue());
        }

        if(strategy.get(2) == 0) {
            validStrategy.add(strategy.get(5).intValue());
        }
        else if(strategy.get(2) == 1) {
            validStrategy.add(strategy.get(4).intValue());
        }

        assert validStrategy.size() == 4 : "Not a valid strategy to return";


        return validStrategy;
    }

    private static int outcomeIndex(List<Integer> strategy) {
        
        int left = 0;
        int right = 0;
        int length = 0;
        if(strategy.size()==4){
            right = 15;
            length = 16;
        }else if(strategy.size()==2){
            right = 3;
            length = 4;
        }

        assert right != 0;

        for(int i=0; i<strategy.size(); i++) {
            if(strategy.get(i) == 0) {
                length /= 2;
                left += length;
            }
            else if(strategy.get(i) == 1) {
                length /= 2;
                right -= length;
            }
        }

        assert left == right : "Index not found yet";

        return left;
    }

    private static <T extends Comparable<T>> T[] getOutcome(List<Integer> strategy, T[][] outcomes) {
        int outcomeIndex = outcomeIndex(strategy);
        return outcomes[outcomeIndex];
    }

    public static void main(String[] args) {

        SparkConf conf = new SparkConf();
        conf.setAppName("GameSolver");
        conf.setMaster("local[*]");
        
        String inputBasePath = getPathToProcess();
        File[] dirsToProcess = getDirsToProcess(inputBasePath);

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTimeString = currentDateTime.format(formatter);
        String resultBasePath = System.getenv("OUTPUT_LABELED");

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
   
            long startTime = System.currentTimeMillis();
            int total = dirsToProcess.length;
            int current = 0;
            
            for(File dir : dirsToProcess) {
                String inputPath = inputBasePath + "/" + dir.getName();
                String resultPath = resultBasePath + "/" + dir.getName();
            
                // actual porcessing
                JavaRDD<String> linesRDD = sc.textFile(inputPath);
                JavaRDD<String> processedRDD = linesRDD.map(line -> processLine(line));
                processedRDD.saveAsTextFile(resultPath);

                // estimate remaining time
                current++;
                long currentTime = System.currentTimeMillis();
                long ellapsedTime = currentTime - startTime;
                long estimatedTimeLeft = ((ellapsedTime / current) * (total - current)) / 3600;
                System.out.println("Estimated time left: " + estimatedTimeLeft);
            }
            
            sc.stop();
        }
    }

    public static String getPathToProcess() {
        String inputPathBase = System.getenv("OUTPUT_GENERATED");
        File inputFolder = new File(inputPathBase);
        File[] subDirs = inputFolder.listFiles(File::isDirectory);

        String inputPath = "";

        if (subDirs != null) {
            for(File subDir : subDirs) {
                System.out.println(subDir.getName());
            }
        }
        else {
            System.out.println("No subdirectories found...");
            return "";
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select subdirectory: ");

        String subDirName = scanner.nextLine();
        scanner.close();
        
        inputPath = inputPathBase + subDirName;

        return inputPath;
    }

    public static File[] getDirsToProcess(String generationPath) {
        File genFolder = new File(generationPath);
        File [] genDirs = genFolder.listFiles(File::isDirectory);

        return genDirs;
    }

    public static String processLine(String line) {
        
        JSONObject input = new JSONObject(line);
        JsonGame<Double> jsonGame = new JsonGame<>(line, Double.class);

        // find nash eq
        JSONArray nashArray = new JSONArray();
        try {
            Set<Object[]> nashEqs = solveNash(jsonGame);
            for (Object[] eq : nashEqs) {
                JSONArray rowArray = new JSONArray();
                Double[] outcome = (Double[])eq[1];
                for(Double val : outcome) {
                    rowArray.put(val);
                }
                nashArray.put(rowArray);
            }
        }
        catch(IllegalAccessException e) {
            e.printStackTrace();
        }

        JSONArray solutionOutcomeArray = new JSONArray();
        JSONArray solutionPathArray = new JSONArray();
        try{
        	Solution<Double> solution = solveWithDoubles(line);
            Double[][] solutionOutcome = solution.getOutcome();
            for(Double[] row : solutionOutcome) {
                JSONArray rowArray = new JSONArray();
                for(Double value : row){
                    rowArray.put(value);
                }
                solutionOutcomeArray.put(rowArray);
            }
            int[] solutionPath = solution.getSolutionPath();
            for(int p : solutionPath)
            {
            	solutionPathArray.put(p);
            }
            
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        JSONObject solutionObject = new JSONObject();
        solutionObject.put("path", solutionPathArray);
        solutionObject.put("outcomes", solutionOutcomeArray);
        input.put("PTE", solutionObject);
        input.put("nash", nashArray);

        return input.toString();
    }

}
