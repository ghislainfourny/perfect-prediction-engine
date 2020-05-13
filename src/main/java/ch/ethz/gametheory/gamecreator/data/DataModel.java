package ch.ethz.gametheory.gamecreator.data;

import ch.ethz.gametheory.gamecreator.ChoiceNode;
import ch.ethz.gametheory.gamecreator.Outcome;
import ch.ethz.gametheory.gamecreator.Tree;
import ch.ethz.gametheory.gamecreator.TreeNode;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableSet;

public class DataModel {

    int numberOfPlayers = 1;
    int numberOfInformationSets = 1;

    ObservableList<Player> players;
    ObservableList<InformationSet> informationSets;
    Set<Outcome> selectedOutcomes;
    Set<ChoiceNode> selectedChoiceNodes;
    ObservableList<Tree> trees;
    DoubleProperty scale;

    public DataModel() {
        players = observableArrayList(e -> new Observable[]{e.nameProperty()});
        informationSets = observableArrayList(e -> new Observable[]{e.playerProperty()});
        trees = observableArrayList();
        selectedOutcomes = observableSet(new HashSet<>());
        selectedChoiceNodes = observableSet(new HashSet<>());
        scale = new SimpleDoubleProperty(0.0);
    }

    public ObservableList<Player> getPlayers() {
        return players;
    }

    public Player addPlayer() {
        Player newPlayer = new Player(numberOfPlayers++);
        players.add(newPlayer);
        return newPlayer;
    }

    public void removePlayer(Player player) {
        player.setDeleted();
        players.remove(player);
    }

    public ObservableList<InformationSet> getInformationSets() {
        return informationSets;
    }

    public InformationSet addInformationSet() {
        InformationSet newInformationSet = new InformationSet(numberOfInformationSets++);
        informationSets.add(newInformationSet);
        return newInformationSet;
    }

    public void removeInformationSet(InformationSet informationSet) {
        informationSet.setDeleted();
        informationSets.remove(informationSet);
    }

    public Outcome addOutcome() {
        cleanSelectedNodes();
        Outcome outcome = new Outcome(this);
        setSelectedNode(outcome);
        Tree tree = new Tree(outcome);
        trees.add(tree);
        return outcome;
    }

    public ChoiceNode addChoiceNode() {
        cleanSelectedNodes();
        ChoiceNode choiceNode = new ChoiceNode(this);
        setSelectedNode(choiceNode);
        Tree tree = new Tree(choiceNode);
        trees.add(tree);
        return choiceNode;
    }

    public Set<ChoiceNode> getSelectedChoiceNodes() {
        return selectedChoiceNodes;
    }

    public Set<Outcome> getSelectedOutcomes() {
        return selectedOutcomes;
    }

    public void cleanSelectedNodes() {
        for (ChoiceNode c : selectedChoiceNodes) {
            c.setSelected(false);
        }
        for (Outcome o : selectedOutcomes) {
            o.setSelected(false);
        }
        selectedChoiceNodes.clear();
        selectedOutcomes.clear();
    }

    public void setSelectedNode(TreeNode treeNode) {
        cleanSelectedNodes();
        if (treeNode instanceof ChoiceNode) {
            selectedChoiceNodes.add((ChoiceNode) treeNode);
        } else {
            selectedOutcomes.add((Outcome) treeNode);
        }
        treeNode.setSelected(true);
    }

    public void toggleSelectedNode(TreeNode treeNode) {
        if (treeNode instanceof ChoiceNode) {
            if (!selectedChoiceNodes.contains(treeNode)) {
                selectedChoiceNodes.add((ChoiceNode) treeNode);
                treeNode.setSelected(true);

            } else {
                selectedChoiceNodes.remove(treeNode);
                treeNode.setSelected(false);
            }
        } else if (treeNode instanceof Outcome) {
            if (!selectedOutcomes.contains(treeNode)) {
                selectedOutcomes.add((Outcome) treeNode);
                treeNode.setSelected(true);
            } else {
                selectedOutcomes.remove(treeNode);
                treeNode.setSelected(false);
            }
        }
    }

    public void deleteSelectedNodes() {
        deleteSelectedOutcomes();
        deleteSelectedChoiceNodes();
    }

    private void deleteSelectedOutcomes() {
        for (Outcome o : selectedOutcomes) {
            o.getTree().deleteNode(o);
        }
    }

    private void deleteSelectedChoiceNodes() {
        Object[] selected = selectedChoiceNodes.toArray();
        for (int i = 0; i < selected.length; i++) {
            Object t = selected[i];
            if (t instanceof TreeNode) {
                List<Tree> childrenTrees = ((TreeNode) selected[i]).getTree().deleteNode((TreeNode) t);
                if (childrenTrees != null) trees.addAll(childrenTrees);
            }
        }
    }

    public ObservableList<Tree> getTrees() {
        return trees;
    }

    public DoubleProperty scaleProperty() {
        return scale;
    }

}
