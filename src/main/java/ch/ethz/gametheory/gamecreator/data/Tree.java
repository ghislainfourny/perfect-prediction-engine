package ch.ethz.gametheory.gamecreator.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Tree {

    private final TreeNode root;
    private final BooleanProperty integrityChange = new SimpleBooleanProperty();
    private final BooleanProperty structureChange = new SimpleBooleanProperty();
    private boolean deleted;

    public Tree(TreeNode root) {
        this.root = root;
        root.assignTree(this);
        toggleStructureChange();
    }

    public boolean isDeleted() {
        return deleted;
    }

    void setDeleted() {
        deleted = true;
    }

    public BooleanProperty structureChangeProperty() {
        return structureChange;
    }

    void toggleStructureChange() {
        this.structureChange.set(!this.structureChange.get());
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
