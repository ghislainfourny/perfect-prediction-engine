package ch.ethz.gametheory.gamecreator.xmlhelper;

import ch.ethz.gametheory.gamecreator.ChoiceNode;
import ch.ethz.gametheory.gamecreator.Outcome;
import ch.ethz.gametheory.gamecreator.Tree;
import ch.ethz.gametheory.gamecreator.TreeNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeXML {
    public int rootNode;
    public List<ChoiceNodeXML> choiceNodes;
    public List<OutcomeXML> outcomes;

    public static TreeXML convert(Tree tree, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter){
        if (tree == null) {
            return null;
        }

        TreeXML treeXML = new TreeXML();
        treeXML.choiceNodes = new LinkedList<>();
        treeXML.outcomes = new LinkedList<>();
        treeXML.rootNode = uniqueIdentifier.getOrDefault(tree.getRoot(), counter.getAndIncrement());
        uniqueIdentifier.put(tree.getRoot(), treeXML.rootNode);
        tree.getNodes().forEach(node -> {
            if (node instanceof ChoiceNode){
                ChoiceNodeXML choiceNodeXML = ChoiceNodeXML.convert((ChoiceNode) node, uniqueIdentifier, counter);
                treeXML.choiceNodes.add(choiceNodeXML);
            } else if (node instanceof Outcome) {
                OutcomeXML o = OutcomeXML.convert((Outcome) node,uniqueIdentifier,counter);
                treeXML.outcomes.add(o);
            }
        });
        return treeXML;

    }

}
