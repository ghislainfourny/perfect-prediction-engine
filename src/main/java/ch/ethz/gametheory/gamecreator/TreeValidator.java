package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.data.*;
import ch.ethz.gametheory.ptesolver.GameFactory;
import ch.ethz.gametheory.ptesolver.GameWithImperfectInformation;
import ch.ethz.gametheory.ptesolver.NaiveGameFactory;
import ch.ethz.gametheory.ptesolver.PTESolver;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeValidator {

    private final Tree tree;
    private GameWithImperfectInformation game;
    private Map<Player, Integer> playerToNum;
    Map<InformationSet, Integer> informationSetToNum;
    Map<TreeNode, Integer> shapeToNum;
    List<Integer> choiceNodeToInformationSet;
    List<Integer> informationSetToPlayer;
    List<Integer[]> outcomes;
    List<Integer[]> partialActions;

    int numOfChoiceNodes;
    int numOfInformationSets;
    int numOfPlayers;

    public TreeValidator(Tree tree) {
        this.tree = tree;
        transformTree();
    }

    private void transformTree() {
        choiceNodeToInformationSet = new LinkedList<>();
        informationSetToPlayer = new LinkedList<>();

        playerToNum = new HashMap<>();
        informationSetToNum = new HashMap<>();
        shapeToNum = new HashMap<>();

        numOfChoiceNodes = 0;
        numOfInformationSets = 0;
        numOfPlayers = 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(tree.getRoot());
        while (!queue.isEmpty()) {
            TreeNode parent = queue.poll();
            if (parent instanceof ChoiceNode) {
                queue.addAll(((ChoiceNode) parent).getGraphChildren());
                shapeToNum.put(parent, numOfChoiceNodes++);
                InformationSet temp = ((ChoiceNode) parent).getInformationSet();
                if (temp != null && !informationSetToNum.containsKey(temp)) {
                    informationSetToNum.put(temp, numOfInformationSets++);
                    if (temp.getAssignedPlayer() != null && !playerToNum.containsKey(temp.getAssignedPlayer())) {
                        playerToNum.put(temp.getAssignedPlayer(), numOfPlayers++);
                    }
                    informationSetToPlayer.add(playerToNum.get(temp.getAssignedPlayer()));
                }
                choiceNodeToInformationSet.add(informationSetToNum.get(temp));
            }
        }

        outcomes = new LinkedList<>();
        partialActions = new LinkedList<>();
        Map<InformationSet, Integer> firstInformationSetAction = new HashMap<>();

        int numOfActions = 0;

        queue.add(tree.getRoot());
        while (!queue.isEmpty()) {
            TreeNode parent = queue.poll();
            if (parent instanceof ChoiceNode) {
                List<TreeNode> children = ((ChoiceNode) parent).getGraphChildren();
                queue.addAll(children);
                int tempActionNum;

                if (firstInformationSetAction.containsKey(((ChoiceNode) parent).getInformationSet())) {
                    tempActionNum = firstInformationSetAction.get(((ChoiceNode) parent).getInformationSet());
                } else {
                    firstInformationSetAction.put(((ChoiceNode) parent).getInformationSet(), numOfActions);
                    tempActionNum = numOfActions;
                }

                for (TreeNode child : children) {
                    if (child instanceof Outcome)
                        shapeToNum.put(child, numOfChoiceNodes++);
                    partialActions.add(new Integer[]{tempActionNum++, shapeToNum.get(parent), shapeToNum.get(child)});
                }

                if (tempActionNum > numOfActions)
                    numOfActions = tempActionNum;

            } else {
                Integer[] outcome = new Integer[numOfPlayers];
                for (Player p : playerToNum.keySet()) {
                    outcome[playerToNum.get(p)] = ((Outcome) parent).getPlayerOutcome(p);
                }
                outcomes.add(outcome);
            }
        }
    }

    public String checkConstraints() {
        int[] primitiveChoiceNodeToInformationSet = new int[choiceNodeToInformationSet.size()];
        for (int i = 0; i < primitiveChoiceNodeToInformationSet.length; i++) {
            Integer integer = choiceNodeToInformationSet.get(i);
            if (integer == null) {
                return "Not all choice node have information sets assigned!";
            }
            primitiveChoiceNodeToInformationSet[i] = integer;
        }
        int[] primitiveInformationSetToPlayer = new int[numOfInformationSets];
        for (int i = 0; i < primitiveInformationSetToPlayer.length; i++) {
            Integer integer = informationSetToPlayer.get(i);
            if (integer == null) {
                AtomicInteger id = new AtomicInteger();
                int finalI = i;
                informationSetToNum.forEach((informationSet, integer1) -> {
                    if (integer1 == finalI) {
                        id.set(informationSet.getId());
                    }
                });
                return "Information set " + id.get() + " does not have a player assigned";
            }
            primitiveInformationSetToPlayer[i] = integer;
        }
        int[][] primitivePartialActions = new int[partialActions.size()][3];
        for (int i = 0; i < primitivePartialActions.length; i++) {
            Integer[] temp = partialActions.get(i);
            primitivePartialActions[i][0] = temp[0];
            primitivePartialActions[i][1] = temp[1];
            primitivePartialActions[i][2] = temp[2];
        }

        int[][] primitiveOutcomes = new int[outcomes.size()][numOfPlayers];
        for (int i = 0; i < primitiveOutcomes.length; i++)
            for (int j = 0; j < primitiveOutcomes[i].length; j++)
                primitiveOutcomes[i][j] = outcomes.get(i)[j];
        GameFactory gameFactory = new NaiveGameFactory();
        try {
            gameFactory.parseData(
                    primitiveChoiceNodeToInformationSet,
                    primitiveInformationSetToPlayer,
                    primitivePartialActions,
                    primitiveOutcomes
            );
            game = gameFactory.createGame();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    public void solve() {
        if (game != null) {
            PTESolver gameSolver = new PTESolver(game);
            int[][] solutions = gameSolver.solve();
            Player[] players = new Player[playerToNum.size()];
            playerToNum.forEach((player, integer) -> players[integer] = player);
            tree.setSolution(solutions, players);
        }
    }

}
