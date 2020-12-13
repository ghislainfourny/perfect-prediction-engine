package ch.ethz.gametheory.ptesolver.json;

import ch.ethz.gametheory.ptesolver.GameWithImperfectInformationData;
import ch.ethz.gametheory.ptesolver.GenericUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonGame<T extends Comparable<T>> implements GameWithImperfectInformationData<T> {

    private static final String PLAYER_KEY = "player";
    private static final String INFORMATION_SET_KEY = "information-set";
    private static final String CHILDREN_KEY = "children";
    private static final String PAYOFF_KEY = "payoffs";

    private final Class<T> clazz;

    private final Map<Integer, Integer> externalToInternalPlayerMap = new HashMap<>();
    private final Map<Integer, Integer> externalToInternalInformationSetMap = new HashMap<>();
    private final Map<JSONObject, Integer> externalToInternalChoiceNodeMap = new LinkedHashMap<>();
    private final Map<JSONObject, Integer> externalToInternalOutcomeMap = new HashMap<>();
    private final Map<JSONObject, LinkedList<JSONObject>> nodeChildren = new HashMap<>();
    private final Map<Integer, LinkedList<Integer>> informationSetToActionNumbers = new HashMap<>();
    private final AtomicInteger actionNumberCounter = new AtomicInteger();
    private final LinkedList<Integer> choiceNodeToInformationSetMap = new LinkedList<>();
    private final Map<Integer, Integer> informationSetToPlayerMap = new TreeMap<>();
    private final LinkedList<T[]> payoffs = new LinkedList<>();


    public JsonGame(final String json, final Class<T> clazz) {
        this.clazz = clazz;
        JSONObject jsonObject = new JSONObject(json);
        handleNode(jsonObject);
    }

    private void handleNode(final JSONObject jsonObject) {
        if (jsonObject.isNull(PAYOFF_KEY)) {
            handleChoiceNode(jsonObject);
        } else {
            handleOutcome(jsonObject);
        }
    }

    private void handleOutcome(final JSONObject jsonObject) {
        externalToInternalOutcomeMap.put(jsonObject, externalToInternalOutcomeMap.size());

        JSONArray jsonArray = jsonObject.getJSONArray(PAYOFF_KEY);
        int length = jsonArray.length();
        T[] payoffs = GenericUtils.getGenericArray(clazz, length);
        for (int i = 0; i < jsonArray.length(); i++) {
            payoffs[i] = clazz.cast(jsonArray.get(i));
        }
        this.payoffs.add(payoffs);
    }

    private void handleChoiceNode(final JSONObject jsonObject) {
        externalToInternalChoiceNodeMap.put(jsonObject, externalToInternalChoiceNodeMap.size());

        int internalPlayerNumber = getInternalPlayerNumber(jsonObject);
        int informationSetNumber = getInternalInformationSetNumber(jsonObject, internalPlayerNumber);

        LinkedList<JSONObject> children = new LinkedList<>();
        JSONArray jsonArray = jsonObject.getJSONArray(CHILDREN_KEY);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject child = jsonArray.getJSONObject(i);
            children.add(child);
            handleNode(child);
        }

        nodeChildren.put(jsonObject, children);
    }

    private int getInternalInformationSetNumber(final JSONObject jsonObject, final int internalPlayerNumber) {
        int informationSetNumber = jsonObject.getInt(INFORMATION_SET_KEY);
        externalToInternalInformationSetMap.putIfAbsent(informationSetNumber, externalToInternalInformationSetMap.size());
        Integer internalInformationSetNumber = externalToInternalInformationSetMap.get(informationSetNumber);
        choiceNodeToInformationSetMap.add(internalInformationSetNumber);
        informationSetToPlayerMap.putIfAbsent(internalInformationSetNumber, internalPlayerNumber);

        if (!informationSetToActionNumbers.containsKey(internalInformationSetNumber)) {
            JSONArray jsonArray = jsonObject.getJSONArray(CHILDREN_KEY);
            LinkedList<Integer> actionNumbers = new LinkedList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                actionNumbers.add(actionNumberCounter.getAndIncrement());
            }
            informationSetToActionNumbers.put(internalInformationSetNumber, actionNumbers);
        }

        return internalInformationSetNumber;
    }

    private int getInternalPlayerNumber(final JSONObject jsonObject) {
        int playerNumber = jsonObject.getInt(PLAYER_KEY);
        externalToInternalPlayerMap.putIfAbsent(playerNumber, externalToInternalPlayerMap.size());
        return externalToInternalPlayerMap.get(playerNumber);
    }


    @Override
    public int[] getChoiceNodeToInformationSetMap() {
        int[] choiceNodeToInformationSetMap = new int[this.choiceNodeToInformationSetMap.size()];
        for (int i = 0; i < choiceNodeToInformationSetMap.length; i++) {
            choiceNodeToInformationSetMap[i] = this.choiceNodeToInformationSetMap.get(i);
        }
        return choiceNodeToInformationSetMap;
    }

    @Override
    public int[] getInformationSetToPlayerMap() {
        int[] choiceNodeToPlayerMap = new int[informationSetToPlayerMap.size()];
        for (final Map.Entry<Integer, Integer> entry : informationSetToPlayerMap.entrySet()) {
            choiceNodeToPlayerMap[entry.getKey()] = entry.getValue();
        }
        return choiceNodeToPlayerMap;
    }

    @Override
    public int[][] getPartialActions() {
        int numberOfChoiceNodes = externalToInternalChoiceNodeMap.size();
        int numberOfPartialActions = numberOfChoiceNodes + externalToInternalOutcomeMap.size() - 1;
        int[][] partialActions = new int[numberOfPartialActions][3];
        int counter = 0;
        for (final Map.Entry<JSONObject, Integer> entry : externalToInternalChoiceNodeMap.entrySet()) {
            JSONObject choiceNode = entry.getKey();
            Integer choiceNodeNumber = entry.getValue();
            LinkedList<JSONObject> jsonObjects = nodeChildren.get(choiceNode);
            LinkedList<Integer> actionNumbers = informationSetToActionNumbers.get(choiceNodeToInformationSetMap.get(choiceNodeNumber));
            for (int i = 0; i < actionNumbers.size(); i++) {
                partialActions[counter][0] = actionNumbers.get(i);
                partialActions[counter][1] = choiceNodeNumber;
                JSONObject child = jsonObjects.get(i);
                if (externalToInternalChoiceNodeMap.containsKey(child)) {
                    partialActions[counter][2] = externalToInternalChoiceNodeMap.get(child);
                } else {
                    partialActions[counter][2] = externalToInternalOutcomeMap.get(child) + numberOfChoiceNodes;
                }
                counter++;
            }
        }
        return partialActions;
    }

    @Override
    public T[][] getOutcomes() {
        T[][] payoff = GenericUtils.getMatrix(clazz, payoffs.size());
        int counter = 0;
        for (final T[] payoff1 : this.payoffs) {
            payoff[counter++] = payoff1;
        }
        return payoff;
    }

}
