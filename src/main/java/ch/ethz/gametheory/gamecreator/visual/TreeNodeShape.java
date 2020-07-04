package ch.ethz.gametheory.gamecreator.visual;

import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.TreeNode;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import javax.annotation.Nonnull;

public abstract class TreeNodeShape<SHAPE extends Shape, DATA extends TreeNode> extends Group {

    protected static final Color SELECTED_STROKE_COLOR = Color.RED;
    protected static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
    protected static final Color SOLUTION_STROKE_COLOR = Color.GOLD;
    protected static final Color DEFAULT_FILL_COLOR = Color.LIGHTGREY;

    @Nonnull
    private final Model model;
    @Nonnull
    private final SHAPE shape;
    @Nonnull
    private final DATA data;
    @Nonnull
    private final Label label;

    public TreeNodeShape(@Nonnull Model model, @Nonnull DATA data, @Nonnull SHAPE shape, @Nonnull Label label) {
        super(shape, label);
        this.model = model;
        this.shape = shape;
        this.data = data;
        this.label = label;
        initShape();
        initLabel();
        setDragEvents();
        setEvents();
        getData().selectedProperty().addListener((observable, oldValue, newValue) ->
                getShape().setStroke(observable.getValue() ? SELECTED_STROKE_COLOR : getData().isSolution() ? SOLUTION_STROKE_COLOR : DEFAULT_STROKE_COLOR));
    }

    private void initShape() {
        getShape().setStrokeWidth(2.0);
        getShape().setStroke(getData().isSelected() ? SELECTED_STROKE_COLOR : DEFAULT_STROKE_COLOR);
        getShape().setFill(DEFAULT_FILL_COLOR);
    }

    private void initLabel() {
        getLabel().setAlignment(Pos.CENTER);
        getLabel().getStyleClass().remove("label");
    }

    @Nonnull
    protected SHAPE getShape() {
        return shape;
    }

    public abstract double getCenterX();

    public abstract double getCenterY();

    public Point2D getCenter() {
        return new Point2D(getCenterX(), getCenterY());
    }

    public abstract DoubleProperty centerXProperty();

    public abstract DoubleProperty centerYProperty();

    public abstract double centerToBorderDistance(double angle);

    public void setSelected(boolean value) {
        getShape().setStroke(value ? SELECTED_STROKE_COLOR : DEFAULT_STROKE_COLOR);
    }

    @Nonnull
    public DATA getData() {
        return data;
    }

    @Nonnull
    protected Model getModel() {
        return model;
    }

    @Nonnull
    protected Label getLabel() {
        return label;
    }

    protected void setDragEvents() {
        this.setOnDragDetected(mouseEvent -> {
            this.startFullDrag();
            getModel().setOriginNodeShape(this);
            mouseEvent.consume();
        });
        this.setOnMouseDragEntered(mouseDragEvent -> {
            getModel().setTargetNodeShape(this);
            mouseDragEvent.consume();
        });
        this.setOnMouseExited(mouseEvent -> {
            getModel().setTargetNodeShape(null);
            mouseEvent.consume();
        });
        this.setOnMouseDragReleased(mouseDragEvent -> {
            getModel().commitDragAndDropAction();
            mouseDragEvent.consume();
        });
        this.setOnMouseDragOver(mouseDragEvent -> {
            if (mouseDragEvent.getSource() instanceof TreeNodeShape) {
                getModel().setTargetNodeShape((TreeNodeShape<?, ?>) mouseDragEvent.getSource());
            } else {
                getModel().setTargetNodeShape(null);
            }
            mouseDragEvent.consume();
        });
        this.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getPickResult().getIntersectedNode() instanceof TreeNodeShape) {
                getModel().setTargetNodeShape((TreeNodeShape<?, ?>) mouseEvent.getPickResult().getIntersectedNode());
            }
            mouseEvent.consume();
        });
    }

    private void setEvents() {
        this.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.isControlDown()) {
                getModel().toggleSelectedNode(getData());
            } else {
                getModel().setSelectedNode(getData());
            }
            mouseEvent.consume();
        });
    }

}
