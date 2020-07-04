package ch.ethz.gametheory.gamecreator.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Tree {

    private final TreeNode root;
    private final BooleanProperty integrityChange;

    public Tree(TreeNode root) {
        this.root = root;
        root.assignTree(this);
        this.integrityChange = new SimpleBooleanProperty();
        toggleIntegrityChange();
    }

    public BooleanProperty integrityChangeProperty() {
        return this.integrityChange;
    }

    void toggleIntegrityChange() {
        this.integrityChange.set(!this.integrityChange.get());
        invalidateSolution();
    }

    public TreeNode getRoot() {
        return root;
    }

    public boolean contains(TreeNode treeNode) {
        return getRoot().isDescendant(treeNode);
    }

    void invalidateSolution() {
        getRoot().invalidateSolution();
    }

    public void setSolution(int[][] outcomes, Player[] players) {
        getRoot().findSolution(outcomes, players);
    }

}
