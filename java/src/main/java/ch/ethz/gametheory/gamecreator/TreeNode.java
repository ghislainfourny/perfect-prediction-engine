package ch.ethz.gametheory.gamecreator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Shape;

public abstract class TreeNode extends Group {

    private BooleanProperty solutionProperty;
    private BooleanProperty changedProperty;
    private BooleanProperty isDeleted;
    private Tree tree;

    TreeNode(Shape shape, Label label) {
        super(shape, label);
        this.solutionProperty = new SimpleBooleanProperty(false);
        this.changedProperty = new SimpleBooleanProperty(false);
    }

    public Tree getTree() {
        return tree;
    }
    public void setTree(Tree tree) {
        this.tree = tree;
    }
    public abstract double getCenterX();
    public abstract double getCenterY();
    public abstract DoubleProperty centerXProperty();
    public abstract DoubleProperty centerYProperty();
    public abstract void setSelected(boolean value);
    public void setSolution(boolean value) {
        this.solutionProperty.set(value);
    }
    public BooleanProperty solutionProperty() {
        return this.solutionProperty;
    }
    public BooleanProperty changedProperty() {
        return this.changedProperty;
    }
    protected void toggleChanged() {
        this.changedProperty.set(!this.changedProperty.get());
    }
    public abstract BooleanProperty deletedProperty();

}