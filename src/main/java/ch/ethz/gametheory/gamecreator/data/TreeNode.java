package ch.ethz.gametheory.gamecreator.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class TreeNode {

    private final BooleanProperty selected;
    private final BooleanProperty solution;
    private final StringProperty outputText;
    private Tree tree;
    private ChoiceNode parentNode;

    public TreeNode() {
        this.selected = new SimpleBooleanProperty(false);
        this.solution = new SimpleBooleanProperty(false);
        this.outputText = new SimpleStringProperty("");
    }

    public void setSelected(boolean value) {
        selected.setValue(value);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSolution(boolean value) {
        solution.set(value);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    abstract void invalidateSolution();

    abstract void findSolution(int[][] outcomes, Player[] players);

    public boolean isSolution() {
        return solution.get();
    }

    public BooleanProperty solutionProperty() {
        return solution;
    }

    public StringProperty getOutputText() {
        return outputText;
    }

    @Nonnull
    public Tree getTree() {
        return tree;
    }

    void setTree(@Nonnull Tree tree) {
        this.tree = tree;
    }

    abstract void assignTree(@Nonnull Tree tree);

    @Nullable
    public ChoiceNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(@Nullable ChoiceNode parentNode) {
        this.parentNode = parentNode;
    }

    abstract boolean isDescendant(TreeNode treeNode);

    public Tree detachFromTree() {
        ChoiceNode parentNode = getParentNode();
        setParentNode(null);
        Tree tree = getTree();
        if (parentNode != null) {
            parentNode.removeChild(this);
        }
        if (tree.getRoot() == this) {
            tree.setDeleted();
        }
        Tree newTree = new Tree(this);
        tree.toggleStructureChange();
        return newTree;
    }

    public abstract List<Tree> deleteNode();

}