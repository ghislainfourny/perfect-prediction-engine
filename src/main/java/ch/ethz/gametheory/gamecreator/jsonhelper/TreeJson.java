package ch.ethz.gametheory.gamecreator.jsonhelper;

import ch.ethz.gametheory.gamecreator.data.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeJson {
    public int rootNode;
    public List<ChoiceNodeJson> choiceNodes;
    public List<OutcomeJson> outcomes;

    public static TreeJson convert(Tree tree, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter, Model model) {
        if (tree == null) {
            return null;
        }

        TreeJson treeJson = new TreeJson();
        treeJson.choiceNodes = new LinkedList<>();
        treeJson.outcomes = new LinkedList<>();
        TreeNode root = tree.getRoot();
        treeJson.rootNode = uniqueIdentifier.getOrDefault(root, counter.getAndIncrement());
        uniqueIdentifier.put(root, treeJson.rootNode);
        addNodes(root, uniqueIdentifier, counter, model, treeJson);
        return treeJson;
    }

    private static void addNodes(TreeNode treeNode, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter, Model model, TreeJson treeJson) {
        if (treeNode instanceof ChoiceNode) {
            ChoiceNodeJson choiceNodeXML = ChoiceNodeJson.convert((ChoiceNode) treeNode, uniqueIdentifier, counter);
            treeJson.choiceNodes.add(choiceNodeXML);
            ((ChoiceNode) treeNode).getGraphChildren().forEach(treeNode1 -> addNodes(treeNode1, uniqueIdentifier, counter, model, treeJson));
        } else if (treeNode instanceof Outcome) {
            OutcomeJson o = OutcomeJson.convert((Outcome) treeNode, uniqueIdentifier, counter, model);
            treeJson.outcomes.add(o);
        }
    }
}
