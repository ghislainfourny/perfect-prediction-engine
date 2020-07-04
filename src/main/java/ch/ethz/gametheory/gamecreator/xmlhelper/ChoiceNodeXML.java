package ch.ethz.gametheory.gamecreator.xmlhelper;

import ch.ethz.gametheory.gamecreator.data.ChoiceNode;
import ch.ethz.gametheory.gamecreator.data.TreeNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChoiceNodeXML {
    public int nodeID;
    public int informationsetID;
    public List<Integer> children;

    public static ChoiceNodeXML convert(ChoiceNode choiceNode, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter) {
        ChoiceNodeXML choiceNodeXML = new ChoiceNodeXML();
        choiceNodeXML.nodeID = uniqueIdentifier.getOrDefault(choiceNode, counter.getAndIncrement());
        uniqueIdentifier.put(choiceNode, choiceNodeXML.nodeID);
        choiceNodeXML.informationsetID = choiceNode.getInformationSet()!=null? choiceNode.getInformationSet().getId():-1;
        choiceNodeXML.children = new LinkedList<>();
        choiceNode.getGraphChildren().forEach(child -> {
            int childID = uniqueIdentifier.getOrDefault(child, counter.getAndIncrement());
            choiceNodeXML.children.add(childID);
            uniqueIdentifier.put(child, childID);
        });
        return choiceNodeXML;
    }

}
