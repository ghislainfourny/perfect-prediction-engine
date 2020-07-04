package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.DragAndDropOperation;
import ch.ethz.gametheory.gamecreator.TreeValidator;
import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.Tree;
import ch.ethz.gametheory.gamecreator.data.TreeNode;
import ch.ethz.gametheory.gamecreator.visual.*;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ForestController {

    // Variables used for connecting two nodes
    private Arrow connectingEdge;

    // Other
    private Model model;

    private ObjectProperty<TreePane> mainTree;

    // Objects defined in forest.fxml

    @FXML
    private Forest BoxUnassignedNodes;
    @FXML
    private HBox MainTreePane;
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane topPane;
    @FXML
    private NotificationBox notificationBox;
    @FXML
    private Button ButtonRotateR;
    @FXML
    private Button ButtonRotateL;
    @FXML
    private ToggleButton ToggleConnecting;
    @FXML
    private ToggleButton ToggleMove;
    @FXML
    private CheckBox ShowPlayersCheckBox;
    @FXML
    private CheckBox ShowPayoutsCheckBox;
    @FXML
    private Button btnCheckConstraints;
    @FXML
    private Button btnSolve;
    private ToggleGroup toggleGroup;

    @Nullable
    public Tree getMainTree() {
        if (mainTree.getValue() == null) return null;
        return mainTree.getValue().getTree();
    }

    @Nullable
    private TreePane removeAndGetMainTreePane() {
        if (!MainTreePane.getChildren().isEmpty()) {
            return (TreePane) MainTreePane.getChildren().remove(0);
        }
        return null;
    }

    public void setMainTreePane(@Nullable TreePane treePane) {
        if (treePane != null) {
            MainTreePane.getChildren().add(treePane);
        }
    }

    @FXML
    private void toggleConnecting() {
        toggleGroup.selectToggle(ToggleConnecting);
    }

    @FXML
    private void toggleMove() {
        toggleGroup.selectToggle(ToggleMove);
    }

    @FXML
    void rotateLeft() {
        TreePane treePane = removeAndGetMainTreePane();
        TreePane newMainTreePane = BoxUnassignedNodes.rotateAnticlockwise(treePane);
        mainTree.setValue(newMainTreePane);
        setMainTreePane(newMainTreePane);
    }

    @FXML
    void rotateRight() {
        TreePane treePane = removeAndGetMainTreePane();
        TreePane newMainTreePane = BoxUnassignedNodes.rotateClockwise(treePane);
        mainTree.setValue(newMainTreePane);
        setMainTreePane(newMainTreePane);
    }

    // Adding nodes or outcomes and add to forest
    private void newTreePosition(TreePane pane) {
        if (mainTree.getValue() == null) {
            mainTree.setValue(pane);
            MainTreePane.getChildren().add(mainTree.getValue());
        } else {
            BoxUnassignedNodes.getChildren().add(pane);
        }
    }

    private void addTreeNode(Tree tree) {
        TreePane treePane = new TreePane(model, tree);
        newTreePosition(treePane);
    }


    public TreePane getTreePane(TreeNode node) {
        if (mainTree.getValue() != null) {
            if (mainTree.getValue().getTree().contains(node)) {
                return mainTree.getValue();
            } else {
                for (Node n : BoxUnassignedNodes.getChildren()) {
                    if (((TreePane) n).getTree().contains(node)) return (TreePane) n;
                }
            }
        }
        return null;
    }

    // Methods for connecting shapes

    private void cutArrow(boolean end) {
        TreeNodeShape<?, ?> originNode = model.getOriginNodeShape();
        TreeNodeShape<?, ?> targetNode = model.getTargetNodeShape();
        Point2D startPoint = originNode.getCenter();
        startPoint = topPane.sceneToLocal(originNode.localToScene(startPoint));
        Point2D endPoint;

        if (end) {
            endPoint = targetNode.getCenter();
            endPoint = topPane.sceneToLocal(targetNode.localToScene(endPoint));
        } else {
            endPoint = new Point2D(connectingEdge.getEndX(),
                    connectingEdge.getEndY());
        }

        double sx = startPoint.getX();
        double sy = startPoint.getY();
        double ex = endPoint.getX();
        double ey = endPoint.getY();

        double direction = (ex - sx) / (ey - sy);
        double distance = originNode.centerToBorderDistance(Math.abs((endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX())));

        connectingEdge.setStartPoint(
                sx + Math.signum(ex - sx) * Math.sqrt((Math.pow(distance, 2) / (1 + Math.pow(1 / direction, 2)))),
                sy + Math.signum(ey - sy) * Math.sqrt((Math.pow(distance, 2) / (1 + Math.pow(direction, 2))))
        );

        if (end) {
            distance = targetNode.centerToBorderDistance(Math.abs((endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX())));
            connectingEdge.setEndPoint(
                    ex + Math.signum(sx - ex) * Math.sqrt((Math.pow(distance, 2) / (1 + Math.pow(1 / direction, 2)))),
                    ey + Math.signum(sy - ey) * Math.sqrt((Math.pow(distance, 2) / (1 + Math.pow(direction, 2))))
            );
        }
    }

    private void startConnecting(@Nonnull TreeNodeShape<?, ?> shape) {
        if (toggleGroup.getSelectedToggle() != ToggleConnecting || shape instanceof ChoiceNodeShape) {

            Point2D originCenter = topPane.sceneToLocal(shape.localToScene(shape.getCenter()));

            if (toggleGroup.getSelectedToggle() == ToggleConnecting) {
                connectingEdge = new ConnectArrow();
            } else if (toggleGroup.getSelectedToggle() == ToggleMove) {
                connectingEdge = new SwapArrow();
            }

            connectingEdge.setStartPoint(originCenter.getX(), originCenter.getY());
            topPane.getChildren().add(connectingEdge);
        }
    }

    public void updateConnecting(double x, double y) {
        if (connectingEdge != null) {
            TreeNodeShape<?, ?> targetNodeShape = model.getTargetNodeShape();
            if (targetNodeShape != null) {
                Point2D point2D = topPane.localToScene(targetNodeShape.sceneToLocal(targetNodeShape.getCenter()));
                connectingEdge.setEndPoint(point2D.getX(), point2D.getX());
                cutArrow(true);
            } else {
                connectingEdge.setEndPoint(x, y);
                cutArrow(false);
            }
        }
    }

    @FXML
    private void checkConstraints() {
        this.notificationBox.clearNotifications();
        TreeValidator treeValidator = new TreeValidator(mainTree.get().getTree());
        String message = treeValidator.checkConstraints();
        mainTree.get().setPassedCheck(message.isEmpty());
        if (!message.isEmpty()) {
            notificationBox.addNotification(message);
        }
    }

    @FXML
    private void solve() {
        model.cleanSelectedNodes();
    }

    private void setTextProperties(Text text) {
        text.setScaleX(1.5);
        text.setScaleY(1.5);
    }

    private void removeTree(Tree tree) {
        if (tree == getMainTree()) {
            if (!MainTreePane.getChildren().isEmpty() && !MainTreePane.getChildren().isEmpty()) {
                mainTree.setValue((TreePane) MainTreePane.getChildren().remove(0));
            }
        } else {
            ObservableList<Node> children = BoxUnassignedNodes.getChildren();
            TreePane toDelete = null;
            for (Node n : children) {
                TreePane treePane = (TreePane) n;
                if (treePane.getTree() == tree) {
                    toDelete = treePane;
                }
            }
            children.remove(toDelete);
        }
    }

    // Initialize controller
    void init(Model model) {
        this.model = model;
        this.model.addForestListener(change -> {
            while (change.next()) {
                change.getRemoved().forEach(this::removeTree);
                change.getAddedSubList().forEach(this::addTreeNode);
            }
        });

        this.BoxUnassignedNodes.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));

        this.stackPane.setOnMouseDragReleased(mouseDragEvent -> {
            model.setTargetNodeShape(null);
            model.setOriginNodeShape(null);
            topPane.getChildren().clear();

        });
        this.stackPane.setOnMouseDragOver(event -> {
            model.setTargetNodeShape(null);
            updateConnecting(event.getX(), event.getY());
        });
        model.originNodeShapeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                topPane.getChildren().clear();
                model.setTargetNodeShape(null);
            } else {
                startConnecting(newValue);
            }
        });
        model.targetNodeShapeProperty().addListener((observable, oldValue, newValue) -> {
                    System.out.println("newValue " + newValue);
                    updateConnecting(0, 0);
                }
        );

        initializeDragAndDropToggle();
        setIcons();

        model.showPlayersProperty().bind(ShowPlayersCheckBox.selectedProperty());
        model.showPayoutsProperty().bind(ShowPayoutsCheckBox.selectedProperty());

        this.mainTree = new SimpleObjectProperty<>(null);
        this.btnCheckConstraints.setDisable(true);
        this.btnSolve.disableProperty().bind(btnCheckConstraints.disableProperty().not().or(mainTree.isNull()));

        this.mainTree.addListener(((observableValue, treePane, t1) -> {
            if (observableValue.getValue() == null) {
                btnCheckConstraints.disableProperty().unbind();
                btnCheckConstraints.setDisable(true);
            } else {
                btnCheckConstraints.disableProperty().bind(observableValue.getValue().passedCheckProperty());
            }
        }));

    }

    private void initializeDragAndDropToggle() {
        this.toggleGroup = new ToggleGroup();
        ToggleMove.setToggleGroup(toggleGroup);
        ToggleConnecting.setToggleGroup(toggleGroup);
        toggleConnecting();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == ToggleConnecting) {
                model.setDragAndDropOperation(DragAndDropOperation.CONNECT);
            } else if (newValue == ToggleMove) {
                model.setDragAndDropOperation(DragAndDropOperation.SWAP);
            }
        });
    }

    private void setIcons() {
        Text tempText = GlyphsDude.createIcon(FontAwesomeIcon.SHARE);
        setTextProperties(tempText);
        ButtonRotateL.setGraphic(tempText);
        tempText = GlyphsDude.createIcon(FontAwesomeIcon.REPLY);
        setTextProperties(tempText);
        ButtonRotateR.setGraphic(tempText);
        tempText = GlyphsDude.createIcon(FontAwesomeIcon.EXTERNAL_LINK_SQUARE);
        setTextProperties(tempText);
        ToggleConnecting.setGraphic(tempText);
        tempText = GlyphsDude.createIcon(FontAwesomeIcon.ARROWS_ALT);
        setTextProperties(tempText);
        ToggleMove.setGraphic(tempText);
    }

}
