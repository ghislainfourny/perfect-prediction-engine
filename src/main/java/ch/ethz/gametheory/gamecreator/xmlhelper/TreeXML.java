package ch.ethz.gametheory.gamecreator.xmlhelper;

import ch.ethz.gametheory.gamecreator.data.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeXML {
    public int rootNode;
    public List<ChoiceNodeXML> choiceNodes;
    public List<OutcomeXML> outcomes;

    public static TreeXML convert(Tree tree, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter, Model model) {
        if (tree == null) {
            return null;
        }

        TreeXML treeXML = new TreeXML();
        treeXML.choiceNodes = new LinkedList<>();
        treeXML.outcomes = new LinkedList<>();
        TreeNode root = tree.getRoot();
        treeXML.rootNode = uniqueIdentifier.getOrDefault(root, counter.getAndIncrement());
        uniqueIdentifier.put(root, treeXML.rootNode);
        addNodes(root, uniqueIdentifier, counter, model, treeXML);
        return treeXML;

    }

    private static void addNodes(TreeNode treeNode, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter, Model model, TreeXML treeXML) {
        if (treeNode instanceof ChoiceNode) {
            ChoiceNodeXML choiceNodeXML = ChoiceNodeXML.convert((ChoiceNode) treeNode, uniqueIdentifier, counter);
            treeXML.choiceNodes.add(choiceNodeXML);
            ((ChoiceNode) treeNode).getGraphChildren().forEach(treeNode1 -> addNodes(treeNode1, uniqueIdentifier, counter, model, treeXML));
        } else if (treeNode instanceof Outcome) {
            OutcomeXML o = OutcomeXML.convert((Outcome) treeNode, uniqueIdentifier, counter, model);
            treeXML.outcomes.add(o);
        }
    }

}
