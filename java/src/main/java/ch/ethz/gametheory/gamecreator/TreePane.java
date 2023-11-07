package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.controllers.ForestController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

import java.util.*;

public class TreePane extends Group {


    private static final int TREE_HORIZONTAL_PADDING = 30;
    private static final int TREE_VERTICAL_PADDING = 30;
    private static final int HORIZONTAL_MARGIN = 10;
    private static final int VERTICAL_MARGIN = 10;

    private BooleanProperty passedCheckProperty;

    private Tree tree;

    private ForestController forestController;

    public TreePane(ForestController forestController, TreeNode root){
        super();
        this.forestController = forestController;
        this.passedCheckProperty = new SimpleBooleanProperty(false);
        this.tree = new Tree(root);
        this.tree.integrityChangeProperty().addListener((observableValue, aBoolean, t1) -> {
            if (getTree().getNodes().isEmpty()){
                forestController.removeTreePane(this);
            } else {
                setPassedCheck(false);
                printTree();
            }
        });

        printTree();
    }

    public TreePane(ForestController forestController, Tree tree) {
        super();
        this.forestController = forestController;
        this.passedCheckProperty = new SimpleBooleanProperty(false);
        this.tree = tree;
        this.tree.integrityChangeProperty().addListener((observableValue, aBoolean, t1) -> {
            if (getTree().getNodes().isEmpty()){
                forestController.removeTreePane(this);
            } else {
                setPassedCheck(false);
                printTree();
            }
        });

        printTree();
    }

    public BooleanProperty passedCheckProperty(){
        return this.passedCheckProperty;
    }

    public void setPassedCheck(boolean value){
        this.passedCheckProperty.set(value);
    }

    public void printTree() {
        Set<TreeNode> graph = getTree().getNodes();
        graph.forEach(this::setEvents);

        this.getChildren().clear();
        setNormalStructure();
    }

    private ObjectBinding<Point2D> getTransformedBinding(TreeNode node, Pane pane){
        return Bindings.createObjectBinding(() -> {
                    Bounds nodeLocal = node.getBoundsInLocal();
                    double x = node.getCenterX()-nodeLocal.getMinX();
                    double y = node.getCenterY()-nodeLocal.getMinY();
                    Bounds nodeScene = node.getLocalToSceneTransform().transform(nodeLocal);
                    Bounds newLocal = pane.sceneToLocal(nodeScene);
                    return new Point2D(newLocal.getMinX() + x, newLocal.getMinY() + y);
                }, node.boundsInLocalProperty(), node.centerXProperty(), node.centerYProperty(),node.localToSceneTransformProperty(),
                pane.localToSceneTransformProperty() , node.localToParentTransformProperty()
        );
    }

    private void setNormalStructure() {
        Pane edges = new Pane();
        edges.setPickOnBounds(false);
        VBox rootBox = new VBox();

        rootBox.setAlignment(Pos.TOP_CENTER);
        rootBox.getChildren().add(tree.getRoot());
        rootBox.setPadding(new Insets(TREE_HORIZONTAL_PADDING, TREE_VERTICAL_PADDING, TREE_HORIZONTAL_PADDING, TREE_VERTICAL_PADDING));

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<VBox> vboxQueue = new LinkedList<>();

        nodeQueue.add(getTree().getRoot());
        vboxQueue.add(rootBox);

        Insets insets = new Insets(VERTICAL_MARGIN, HORIZONTAL_MARGIN,0, HORIZONTAL_MARGIN);

        while (!nodeQueue.isEmpty()){
            VBox parentVBox = vboxQueue.poll();
            TreeNode parentNode = nodeQueue.poll();
            if (parentNode instanceof ChoiceNode){
                List <TreeNode> children = ((ChoiceNode) parentNode).getGraphChildren();
                HBox nextLvl = new HBox();
                nextLvl.setSpacing(HORIZONTAL_MARGIN);
                ObjectBinding<Point2D> parentTransformedBounds = getTransformedBinding(parentNode, edges);

                if (children != null) {
                    for (TreeNode n : children) {
                        VBox temp = new VBox();
                        temp.setAlignment(Pos.TOP_CENTER);
                        temp.setPadding(insets);
                        nodeQueue.add(n);
                        vboxQueue.add(temp);
                        temp.getChildren().add(n);
                        nextLvl.getChildren().add(temp);

                        Line t = new Line();
                        t.setStrokeWidth(2.0);
                        t.startXProperty().bind(Bindings.createDoubleBinding(
                                () -> parentTransformedBounds.get().getX(),
                                parentTransformedBounds
                        ));
                        t.startYProperty().bind(Bindings.createDoubleBinding(
                                () -> parentTransformedBounds.get().getY(),
                                parentTransformedBounds));

                        ObjectBinding<Point2D> boundsInPaneBindingChildren = getTransformedBinding(n, edges);

                        t.endXProperty().bind(Bindings.createDoubleBinding(
                                () -> boundsInPaneBindingChildren.get().getX(),
                                boundsInPaneBindingChildren));
                        t.endYProperty().bind(Bindings.createDoubleBinding(
                                () -> boundsInPaneBindingChildren.get().getY(),
                                boundsInPaneBindingChildren));

                        edges.getChildren().add(t);
                    }
                    assert parentVBox != null;
                    parentVBox.getChildren().add(nextLvl);
                }
            }

        }
        this.getChildren().addAll(edges, rootBox);

    }

    private void setEvents(TreeNode shape) {
        // Selecting
        shape.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.isControlDown()) {
                forestController.toggleSelectedNode(shape);
            } else {
                forestController.setSelectedNode(shape);
            }
            mouseEvent.consume();
        });

        // MouseDrag
        shape.setOnDragDetected(mouseEvent -> {
            shape.startFullDrag();
            forestController.startConnecting((TreeNode) mouseEvent.getSource(),
                    mouseEvent.getSceneX(), mouseEvent.getSceneY());
            mouseEvent.consume();
        });
        shape.setOnMouseDragOver(mouseDragEvent -> {
            forestController.updateConnecting(mouseDragEvent.getSceneX(), mouseDragEvent.getSceneY(),
                    mouseDragEvent.getSource());
            mouseDragEvent.consume();
        });
        shape.setOnMouseDragged(mouseEvent -> {
            forestController.updateConnecting(mouseEvent.getSceneX(), mouseEvent.getSceneY(),
                    mouseEvent.getPickResult().getIntersectedNode());
            mouseEvent.consume();
        });

        shape.setOnMouseDragEntered(mouseDragEvent -> {
            forestController.setTargetNode((TreeNode) mouseDragEvent.getSource());
            mouseDragEvent.consume();
        });
        shape.setOnMouseDragReleased(mouseDragEvent -> {
            forestController.commitDragAndDropAction();
            mouseDragEvent.consume();
        });

    }

    public TreeNode getRootNode(){
        return tree.getRoot();
    }

    public Tree getTree() {
        return tree;
    }

}
