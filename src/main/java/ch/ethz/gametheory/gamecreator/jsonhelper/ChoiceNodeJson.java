package ch.ethz.gametheory.gamecreator.jsonhelper;

import ch.ethz.gametheory.gamecreator.data.ChoiceNode;
import ch.ethz.gametheory.gamecreator.data.TreeNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChoiceNodeJson {
    public int nodeId;
    public int informationSetId;
    public List<Integer> children;

    public static ChoiceNodeJson convert(ChoiceNode choiceNode, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter) {
        ChoiceNodeJson choiceNodeJson = new ChoiceNodeJson();
        choiceNodeJson.nodeId = uniqueIdentifier.getOrDefault(choiceNode, counter.getAndIncrement());
        uniqueIdentifier.put(choiceNode, choiceNodeJson.nodeId);
        choiceNodeJson.informationSetId = choiceNode.getInformationSet() != null ? choiceNode.getInformationSet().getId() : -1;
        choiceNodeJson.children = new LinkedList<>();
        choiceNode.getGraphChildren().forEach(child -> {
            int childID = uniqueIdentifier.getOrDefault(child, counter.getAndIncrement());
            choiceNodeJson.children.add(childID);
            uniqueIdentifier.put(child, childID);
        });
        return choiceNodeJson;
    }
}
