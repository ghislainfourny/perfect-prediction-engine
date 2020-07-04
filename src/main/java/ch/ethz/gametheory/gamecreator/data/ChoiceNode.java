package ch.ethz.gametheory.gamecreator.data;

import javafx.beans.value.ChangeListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChoiceNode extends TreeNode {

    private InformationSet informationSet;
    private final List<TreeNode> children;
    private final ChangeListener<Boolean> informationSetListener;
    private final ChangeListener<Player> playerListener;

    public ChoiceNode() {
        super();
        this.children = new ArrayList<>();
        this.informationSetListener = (observableValue, paint, t1) -> {
            setInformationSet(null);
            toggleChanged();
        };
        this.playerListener = (observableValue, paint, t1) -> toggleChanged();
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
        this.informationSet.deletedProperty().removeListener(informationSetListener);
        this.informationSet.playerProperty().removeListener(playerListener);
        this.informationSet.getColorProperty().unbind();
    }

    public void setInformationSet(@Nullable InformationSet informationSet) {
        if (this.informationSet != informationSet) {
            toggleChanged();
            if (this.informationSet != null)
                cleanInformationSet();

            this.informationSet = informationSet;

            if (informationSet != null) {
                this.informationSet.deletedProperty().addListener(informationSetListener);
                this.informationSet.playerProperty().addListener(playerListener);
            }
        }
    }

    @Nullable
    public InformationSet getInformationSet() {
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
        getTree().toggleIntegrityChange();
    }

    void removeChild(TreeNode node) {
        boolean removed = this.children.remove(node);
        if (removed) {
            toggleChanged();
        }
        getTree().toggleIntegrityChange();
    }

    public List<Tree> deleteNode() {
        final List<Tree> childrenTrees = new LinkedList<>();
        children.forEach(treeNode -> {
            Tree tree = detachFromTree();
            childrenTrees.add(tree);
            removeChild(treeNode);
        });
        ChoiceNode parentNode = getParentNode();
        if (parentNode != null) {
            parentNode.removeChild(this);
        }
        toggleChanged();
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

    private void toggleChanged() {
        getTree().toggleIntegrityChange();
    }

}
