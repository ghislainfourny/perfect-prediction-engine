package ch.ethz.gametheory.gamecreator.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChoiceNode extends TreeNode {

    private final List<TreeNode> children;
    private final ObjectProperty<InformationSet> informationSet = new SimpleObjectProperty<>();
    private final ChangeListener<Boolean> informationSetListener;
    private final ChangeListener<Player> playerListener;

    public ChoiceNode() {
        super();
        this.children = new ArrayList<>();
        this.informationSetListener = (observableValue, paint, t1) -> {
            setInformationSet(null);
            getTree().toggleIntegrityChange();
        };
        this.playerListener = (observableValue, paint, t1) -> getTree().toggleIntegrityChange();
    }

    @Override
    void invalidateSolution() {
        setSolution(false);
        getGraphChildren().forEach(TreeNode::invalidateSolution);
    }

    @Override
    void findSolution(int[][] outcomes, Player[] players) {
        children.forEach(treeNode -> treeNode.findSolution(outcomes, players));
    }

    private void cleanInformationSet() {
        this.informationSet.get().deletedProperty().removeListener(informationSetListener);
        this.informationSet.get().playerProperty().removeListener(playerListener);
    }

    public void setInformationSet(@Nullable InformationSet informationSet) {
        if (this.informationSet.get() != informationSet) {
            getTree().toggleIntegrityChange();
            if (this.informationSet.get() != null) {
                cleanInformationSet();
            }

            this.informationSet.setValue(informationSet);

            if (informationSet != null) {
                this.informationSet.get().deletedProperty().addListener(informationSetListener);
                this.informationSet.get().playerProperty().addListener(playerListener);
            }
        }
    }

    @Nullable
    public InformationSet getInformationSet() {
        return informationSet.get();
    }

    public ObjectProperty<InformationSet> informationSetProperty() {
        return informationSet;
    }

    public List<TreeNode> getGraphChildren() {
        return children;
    }

    public int indexOfChild(@Nonnull TreeNode treeNode) {
        return children.indexOf(treeNode);
    }

    void addChild(@Nonnull TreeNode node, int pos) {
        if (-1 < pos && pos <= this.children.size()) {
            children.add(pos, node);
        } else {
            children.add(node);
        }
        node.setParentNode(this);
        node.assignTree(getTree());
        getTree().toggleStructureChange();
    }

    void removeChild(TreeNode node) {
        boolean removed = this.children.remove(node);
        if (removed) {
            getTree().toggleStructureChange();
        }
        getTree().toggleStructureChange();
    }

    public List<Tree> deleteNode() {
        final List<Tree> childrenTrees = new LinkedList<>();
        for (int i = 0; i < children.size(); i++) {
            TreeNode treeNode = children.get(i);
            Tree tree = detachFromTree();
            childrenTrees.add(tree);
            removeChild(treeNode);
        }
        ChoiceNode parentNode = getParentNode();
        if (parentNode != null) {
            parentNode.removeChild(this);
        }
        if (getTree().getRoot() == this) {
            getTree().setDeleted();
        }
        getTree().toggleStructureChange();
        return childrenTrees;
    }

    @Override
    void assignTree(@Nonnull Tree tree) {
        setTree(tree);
        children.forEach(treeNode -> treeNode.assignTree(tree));
    }

    @Override
    boolean isDescendant(TreeNode treeNode) {
        return treeNode == this || children.parallelStream().anyMatch(treeNode1 -> treeNode1.isDescendant(treeNode));
    }

}
