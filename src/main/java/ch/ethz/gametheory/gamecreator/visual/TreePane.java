package ch.ethz.gametheory.gamecreator.visual;

import ch.ethz.gametheory.gamecreator.TransformationHelper;
import ch.ethz.gametheory.gamecreator.data.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.util.List;
import java.util.Optional;

public class TreePane extends Group {

    private static final int TREE_HORIZONTAL_PADDING = 30;
    private static final int TREE_VERTICAL_PADDING = 30;
    private static final int HORIZONTAL_MARGIN = 10;
    private static final int VERTICAL_MARGIN = 10;
    private static final Insets treeMargin = new Insets(TREE_HORIZONTAL_PADDING, TREE_VERTICAL_PADDING, TREE_HORIZONTAL_PADDING, TREE_VERTICAL_PADDING);
    private static final Insets nodeMargin = new Insets(VERTICAL_MARGIN, HORIZONTAL_MARGIN, 0, HORIZONTAL_MARGIN);

    private final BooleanProperty passedCheckProperty;

    private final Model model;

    private final Tree tree;

    public TreePane(Model model, Tree tree) {
        super();
        this.model = model;
        this.passedCheckProperty = new SimpleBooleanProperty(false);
        this.tree = tree;
        this.tree.integrityChangeProperty().addListener((observableValue, aBoolean, t1) -> {
            if (getTree().getRoot().getTree() != getTree()) {
                model.removeTree(tree);
            } else {
                setPassedCheck(false);
                printTree();
            }
        });

        printTree();
    }

    public BooleanProperty passedCheckProperty() {
        return this.passedCheckProperty;
    }

    public void setPassedCheck(boolean value) {
        this.passedCheckProperty.set(value);
    }

    public void printTree() {
        this.getChildren().clear();
        setNormalStructure();
    }

    private void setNormalStructure() {
        Pane edges = new Pane();
        edges.setPickOnBounds(false);
        Optional<TreeNodeShape<?, ?>> shape = getShape(tree.getRoot());
        shape.ifPresent(treeNodeShape -> {
            VBox vBox = buildVisualTree(treeNodeShape, edges);
            vBox.setPadding(treeMargin);
            this.getChildren().addAll(edges, vBox);
        });
    }

    private VBox buildVisualTree(TreeNodeShape<?, ?> nodeShape, Pane edgesPane) {
        VBox vBox = createNodeChildrenVBox();
        vBox.getChildren().add(nodeShape);
        if (nodeShape instanceof ChoiceNodeShape) {
            final ChoiceNode choiceNode = (ChoiceNode) nodeShape.getData();
            List<TreeNode> graphChildren = choiceNode.getGraphChildren();
            HBox nextLvl = new HBox();
            nextLvl.setSpacing(HORIZONTAL_MARGIN);
            ObjectBinding<Point2D> parentTransformedBounds = TransformationHelper.getTransformedBinding(nodeShape, edgesPane);

            for (TreeNode child : graphChildren) {
                Optional<TreeNodeShape<?, ?>> shape = getShape(child);
                shape.ifPresent(treeNodeShape -> {
                    addEdge(treeNodeShape, parentTransformedBounds, edgesPane);
                    VBox childBox = buildVisualTree(treeNodeShape, edgesPane);
                    nextLvl.getChildren().add(childBox);
                });
            }
            vBox.getChildren().add(nextLvl);
        }
        return vBox;
    }

    private VBox createNodeChildrenVBox() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(nodeMargin);
        return vBox;
    }

    private void addEdge(TreeNodeShape<?, ?> shape, ObjectBinding<Point2D> parentBinding, Pane edgesPane) {
        Line t = new Line();
        t.setStrokeWidth(2.0);
        t.startXProperty().bind(Bindings.createDoubleBinding(
                () -> parentBinding.get().getX(),
                parentBinding
        ));
        t.startYProperty().bind(Bindings.createDoubleBinding(
                () -> parentBinding.get().getY(),
                parentBinding));

        ObjectBinding<Point2D> boundsInPaneBindingChildren = TransformationHelper.getTransformedBinding(shape, edgesPane);

        t.endXProperty().bind(Bindings.createDoubleBinding(
                () -> boundsInPaneBindingChildren.get().getX(),
                boundsInPaneBindingChildren));
        t.endYProperty().bind(Bindings.createDoubleBinding(
                () -> boundsInPaneBindingChildren.get().getY(),
                boundsInPaneBindingChildren));
        edgesPane.getChildren().add(t);
    }


    private Optional<TreeNodeShape<?, ?>> getShape(TreeNode node) {
        if (node instanceof ChoiceNode) {
            return Optional.of(new ChoiceNodeShape(model, (ChoiceNode) node));
        } else if (node instanceof Outcome) {
            return Optional.of(new OutcomeShape(model, (Outcome) node));
        }
        return Optional.empty();
    }

    public Tree getTree() {
        return tree;
    }

}
