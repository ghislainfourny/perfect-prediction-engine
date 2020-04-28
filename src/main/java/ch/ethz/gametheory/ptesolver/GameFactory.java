package ch.ethz.gametheory.ptesolver;

import java.util.*;

public abstract class GameFactory {

    protected final static int ROOT = 0;
    protected final static int PA_ACTION_NUM_INDEX = 0;
    protected final static int PA_FROM_INDEX = 1;
    protected final static int PA_TO_INDEX = 2;

    protected int[] choiceNodeToInformationSetMap;
    protected int[] informationSetToPlayerMap;
    protected int[][] partialActions;
    protected int[][] outcomes;
    protected String[] playerNames;
    protected String[] actionNames;

    /**
     * @return a game with imperfect information wit DependencyStructure, MaximinStructure and OutcomeStructure
     * @throws IllegalAccessException if parseData was not called successfully first
     */
    abstract public GameWithImperfectInformation createGame() throws IllegalAccessException;

    /**
     * see other parseData method
     */
    public void parseData(int[] choiceNodeToInformationSetMap, int[] informationSetToPlayerMap,
                          int[][] partialActions, int[][] outcomes) throws IllegalArgumentException {
        parseData(choiceNodeToInformationSetMap, informationSetToPlayerMap, partialActions, outcomes,null,null);
    }

    /**
     * @param choiceNodeToInformationSetMap A list in which the ith entry is the numerical identifier of the information
     *                                      set of the ith choice node
     * @param informationSetToPlayerMap A list in which the ith entry is the numerical identifier of the player
     *                                  associated with the ith information set
     * @param partialActions A list of partial actions: an action is the combination of all partial actions with the
     *                       same identification number; Important: all partial actions from a choice node need to be
     *                       grouped and within this group the action's identification numbers in order
     *                          1st entry: its identification number
     *                          2nd entry: choice node from which this partial action can be taken
     *                          3rd entry: choice node following this partial action
     * @param outcomes A list of integer outcomes; outcomes[i][j] corresponds to the utility of player j when reaching
     *                 outcome i
     * @param playerNames A list of names for the players; optional; ith entry is the name of the ith player
     * @param strategyNames A list of names for strategies/actions; optional; ith entry is the name of the ith action
     * @throws IllegalArgumentException if the input is not in canonical form or partial actions are not given in the
     *                                  correct order
     */
    public void parseData(int[] choiceNodeToInformationSetMap,int[] informationSetToPlayerMap,
                          int[][] partialActions, int[][] outcomes,
                          String[] playerNames, String[] strategyNames) throws IllegalArgumentException {
        this.choiceNodeToInformationSetMap = choiceNodeToInformationSetMap;
        this.informationSetToPlayerMap = informationSetToPlayerMap;
        this.partialActions = partialActions;
        this.outcomes = outcomes;
        this.playerNames = playerNames;
        this.actionNames = strategyNames;

        if (choiceNodeToInformationSetMap == null ||
                informationSetToPlayerMap == null ||
                partialActions == null ||
                outcomes == null)
            cleanUp("The first four arguments cannot be null!");

        hasValidPlayerNum();
        hasValidInformationSetNum();
        isCanonicalTree();
        hasValidInformationSets();

    }

    /**
     * helper method to clean the state of the factory and throw an exception
     * @param message string to print as exception message
     * @throws IllegalArgumentException always
     */
    private void cleanUp(String message) throws IllegalArgumentException{
        this.choiceNodeToInformationSetMap = null;
        this.informationSetToPlayerMap = null;
        this.partialActions = null;
        this.outcomes = null;
        this.playerNames = null;
        this.actionNames = null;
        throw new IllegalArgumentException(message);
    }

    /**
     * @throws IllegalArgumentException if fields do not correspond to a canonical game tree
     */
    private void isCanonicalTree() throws IllegalArgumentException{


        int n_H = choiceNodeToInformationSetMap.length;
        int n_Z = outcomes.length;
        if (n_H == 0){
            cleanUp("Has no choice node!");
        }

        int[] choiceNodeIndex = new int[n_H];
        Arrays.fill(choiceNodeIndex, -1);
        boolean[] hasStarted = new boolean[n_H];
        int current = -1;
        if (current == partialActions[0][PA_FROM_INDEX])
            cleanUp("Partial action references a non-valid choice node!");
        int actionNum = 0;

        /*
         * checks if partial action is grouped by choice node and sets up index for each choice node so
         * we don't need to scan in the future
         */

        for (int i = 0; i < partialActions.length; i++){
            int[] partialAction = partialActions[i];
            if (partialAction.length != 3)
                cleanUp("Partial action " + i + " does not have right amount of elements!");
            if (current != partialAction[PA_FROM_INDEX]){
                current = partialAction[PA_FROM_INDEX];
                actionNum = partialAction[PA_ACTION_NUM_INDEX];
                if (current < 0 || n_H <= current)
                    cleanUp("Partial action " + i + " has an invalid choice node!");
                if (hasStarted[current])
                    cleanUp("Partial actions are not grouped by choice node!");
                hasStarted[current] = true;
                choiceNodeIndex[current] = i;
            } else {
                if (partialActions[i][PA_ACTION_NUM_INDEX] <= actionNum){
                    cleanUp("Action numbers of choice node " + current + " are not in order!");
                }
                actionNum = partialActions[i][PA_ACTION_NUM_INDEX];
            }
            int toNum = partialAction[PA_TO_INDEX];
            if (toNum < 0 || n_Z + n_H <= toNum)
                cleanUp("Partial action " + i + " has an invalid successor number!");
        }

        for (int i = 0; i < n_H; i++) {
            if (choiceNodeIndex[i] < 0)
                cleanUp("Choice node " + i + " is a leaf!");
        }

        /*
         * check if all nodes and partial actions are reachable by the root
         * if choice node have a unique parent and other small things
         */
        boolean[] reachedNodes = new boolean[choiceNodeToInformationSetMap.length+outcomes.length];
        boolean[] reachedPAs = new boolean[partialActions.length];
        Stack<Integer> choiceNodeStack = new Stack<>();
        choiceNodeStack.push(ROOT);

        while (!choiceNodeStack.isEmpty()){
            int currentNode = choiceNodeStack.pop();
            if (reachedNodes[currentNode])
                cleanUp("Choice node " + currentNode + " does not have a unique parent!");
            reachedNodes[currentNode] = true;
            if (currentNode < n_H){
                for (int i = choiceNodeIndex[currentNode];
                     i < partialActions.length && partialActions[i][PA_FROM_INDEX] == currentNode;
                     i++){
                    reachedPAs[i] = true;
                    int to = partialActions[i][PA_TO_INDEX];
                    choiceNodeStack.push(to);
                }
            }
        }

        for (boolean reachedNode : reachedNodes) {
            if (!reachedNode)
                cleanUp("Not all choice nodes are reachable from the root!");
        }

        for (boolean reachedPA : reachedPAs) {
            if (!reachedPA)
                cleanUp("Not all partial actions are reachable from the root!");
        }

        isCanonical(ROOT, new HashSet<>(), choiceNodeIndex);

    }

    private void hasValidInformationSetNum() {
        int n_I = informationSetToPlayerMap.length;
        for (int i = 0; i < choiceNodeToInformationSetMap.length; i++)
            if (choiceNodeToInformationSetMap[i] < 0
                    || n_I <= choiceNodeToInformationSetMap[i])
                cleanUp("Choice node " + i + " has non-existent information set!");
    }

    private void hasValidPlayerNum() {

        if (0 == outcomes.length){
            cleanUp("There are no outcomes!");
        } else if (0 == outcomes[0].length){
            cleanUp("There are no player or the outcomes are not setup properly!");
        } else if (playerNames != null && playerNames.length != outcomes[0].length){
            cleanUp("playerNames and outcome size does not match up!");
        }

        int n_N = outcomes[0].length;

        for (int[] o : outcomes){
            if (o.length != n_N)
                cleanUp("Not all outcomes have the same amount of payouts!");
        }

        for (int i = 0; i < informationSetToPlayerMap.length; i++) {
            if (informationSetToPlayerMap[i] < 0
                    || n_N <= informationSetToPlayerMap[i])
                cleanUp("Information set " + i + " has non-existent player");
        }

    }

    /**
     * @param node current node
     * @param informationSets set of information sets of ancestors
     * @param startIndex ith index contains the index from where there are partial action of the ith choice node
     * @throws IllegalArgumentException if a ancestor has the same information set
     */
    private void isCanonical(int node, Set<Integer> informationSets, int[] startIndex) {
        int informationSet = choiceNodeToInformationSetMap[node];
        if (informationSets.contains(informationSet))
            cleanUp("Choice node " + node + " has a parent in the same information set!");
        informationSets.add(informationSet);

        for (int i = startIndex[node]; i < partialActions.length && partialActions[i][PA_FROM_INDEX] == node; i++) {
            int to = partialActions[i][PA_TO_INDEX];
            if (to < startIndex.length)
                isCanonical(to, informationSets, startIndex);
        }

        informationSets.remove(informationSet);
    }


    /**
     * @throws IllegalArgumentException if information sets are not set up properly or if there are duplicate action
     *                                  numbers for a choice node
     */
    private void hasValidInformationSets() {
        HashSet<Integer>[] actionsPerInformationSet = new HashSet[informationSetToPlayerMap.length];
        HashSet<Integer>[] actionNumberPerChoiceNode = new HashSet[choiceNodeToInformationSetMap.length];
        for (int i = 0; i < actionNumberPerChoiceNode.length; i++) {
            actionNumberPerChoiceNode[i] = new HashSet<>();
        }
        for (int i = 0; i < actionsPerInformationSet.length; i++) {
            actionsPerInformationSet[i] = new HashSet<>();
        }

        for (int[] partialAction : partialActions) {
            int from = partialAction[PA_FROM_INDEX];
            int actionNum = partialAction[PA_ACTION_NUM_INDEX];
            int informationSet = choiceNodeToInformationSetMap[from];
            HashSet<Integer> actionNums = actionNumberPerChoiceNode[from];
            if (actionNums.contains(actionNum))
                cleanUp("Choice node " + from + " contains several instances of action number "
                        + actionNum + "!");
            actionNums.add(actionNum);
            actionsPerInformationSet[informationSet].add(actionNum);
        }

        // OPTIMIZE can be done more efficiently by only computing size of information set elements once

        for (int i = 0; i < actionNumberPerChoiceNode.length; i++) {
            int informationSet = choiceNodeToInformationSetMap[i];
            if (actionNumberPerChoiceNode[i].size() != actionsPerInformationSet[informationSet].size()){
                cleanUp("Information set "
                        + informationSet + " is not valid!");
            }
        }
    }

}
