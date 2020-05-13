package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.*;
import ch.ethz.gametheory.gamecreator.data.DataModel;
import ch.ethz.gametheory.gamecreator.data.InformationSet;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
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

import java.util.*;

public class ForestController {

    @FXML
    private VBox vboxForest;

    // Variables used for connecting two nodes
    private Arrow connectingEdge;
    private TreeNode originNode;
    private TreeNode targetNode;
    private Point2D targetCenter;

    // Other
    private MainController mainController;
    private DataModel dataModel;

    private static BooleanProperty showPlayers;
    private static BooleanProperty showOutcomes;

    private ObjectProperty<TreePane> mainTree;

    // Objects defined in forest.fxml

    @FXML
    private HBox BoxUnassignedNodes;
    @FXML
    private HBox MainTreePane;
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane topPane;
    @FXML
    private VBox vboxNotifications;
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
    private CheckBox ShowOutcomesCheckBox;
    @FXML
    private Button btnCheckConstraints;
    @FXML
    private Button btnSolve;
    private ToggleGroup toggleGroup;

    public static BooleanProperty showPlayersProperty() {
        return showPlayers;
    }

    public static BooleanProperty showOutcomesProperty() {
        return showOutcomes;
    }

    public Tree getMainTree() {
        if (mainTree.getValue() == null) return null;

        return mainTree.getValue().getTree();
    }

    public List<Tree> getComponents() {
        List<Tree> components = new LinkedList<>();
        BoxUnassignedNodes.getChildren().forEach(node -> {
            if (node instanceof TreePane)
                components.add(((TreePane) node).getTree());
        });
        return components;
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
        if (!BoxUnassignedNodes.getChildren().isEmpty()) {
            this.BoxUnassignedNodes.getChildren().add(this.mainTree.getValue());
            this.MainTreePane.getChildren().clear();
            this.mainTree.setValue((TreePane) this.BoxUnassignedNodes.getChildren().remove(0));
            this.MainTreePane.getChildren().add(this.mainTree.getValue());
        }
    }

    @FXML
    void rotateRight() {
        if (!BoxUnassignedNodes.getChildren().isEmpty()) {
            TreePane temp = (TreePane) BoxUnassignedNodes.getChildren().remove(BoxUnassignedNodes.getChildren().size() - 1);
            BoxUnassignedNodes.getChildren().add(0, mainTree.getValue());
            MainTreePane.getChildren().clear();
            mainTree.setValue(temp);
            MainTreePane.getChildren().add(mainTree.getValue());
        }
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
        TreePane treePane = new TreePane(this, tree);
        newTreePosition(treePane);
    }


    public TreePane getTreePane(TreeNode node) {
        if (mainTree.getValue() != null) {
            if (mainTree.getValue().getTree().getNodes().contains(node)) {
                return mainTree.getValue();
            } else {
                for (Node n : BoxUnassignedNodes.getChildren()) {
                    if (((TreePane) n).getTree().getNodes().contains(node)) return (TreePane) n;
                }
            }
        }
        return null;
    }

    // Methods for connecting shapes

    private Point2D shapeCenter(TreeNode shape) {
        return new Point2D(shape.getCenterX(), shape.getCenterY());
    }

    private double centerToBorderDistance(TreeNode shape, Point2D startPoint, Point2D endPoint) {
        if (shape instanceof ChoiceNode) {
            return ((ChoiceNode) shape).getRadius();
        } else {
            double width = ((Outcome) shape).getRectangleWidth();
            double height = ((Outcome) shape).getRectangleHeight();
            double ratio = Math.abs((endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX()));
            if (height / width > ratio) {
                return Math.sqrt(Math.pow(width / 2, 2) + Math.pow(width / 2 * ratio, 2));
            } else {
                return Math.sqrt(Math.pow(height / 2, 2) + Math.pow(width / 2 / ratio, 2));
            }
        }
    }

    private void cutArrow(boolean end) {
        Point2D startPoint = shapeCenter(originNode);
        startPoint = topPane.sceneToLocal(originNode.localToScene(startPoint));
        Point2D endPoint;

        if (end) {
            endPoint = shapeCenter(targetNode);
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
        double distance = centerToBorderDistance(originNode, startPoint, endPoint);

        connectingEdge.setStartPoint(
                sx + Math.signum(ex - sx) * Math.sqrt((Math.pow(distance, 2) / (1 + Math.pow(1 / direction, 2)))),
                sy + Math.signum(ey - sy) * Math.sqrt((Math.pow(distance, 2) / (1 + Math.pow(direction, 2))))
        );

        if (end) {
            distance = centerToBorderDistance(targetNode, endPoint, startPoint);
            connectingEdge.setEndPoint(
                    ex + Math.signum(sx - ex) * Math.sqrt((Math.pow(distance, 2) / (1 + Math.pow(1 / direction, 2)))),
                    ey + Math.signum(sy - ey) * Math.sqrt((Math.pow(distance, 2) / (1 + Math.pow(direction, 2))))
            );
        }
    }

    public void startConnecting(TreeNode shape, double x, double y) {

        originNode = shape;

        if (toggleGroup.getSelectedToggle() != ToggleConnecting || shape instanceof ChoiceNode) {

            Point2D originCenter = topPane.sceneToLocal(shape.localToScene(shapeCenter(shape)));
            setTargetNode(shape);

            if (toggleGroup.getSelectedToggle() == ToggleConnecting) {

                connectingEdge = new Arrow(false);
                connectingEdge.setFill(Color.RED);

            } else if (toggleGroup.getSelectedToggle() == ToggleMove) {

                connectingEdge = new Arrow(true);
                connectingEdge.setFill(Color.DARKSLATEBLUE);

            }

            connectingEdge.setStartPoint(originCenter.getX(), originCenter.getY());
            updateConnecting(x, y, null);
            topPane.getChildren().add(connectingEdge);
        }
    }

    public void setTargetNode(TreeNode shape) {
        targetNode = shape;
        targetCenter = topPane.sceneToLocal(shape.localToScene(shape.getCenterX(), shape.getCenterY()));
    }

    public void updateConnecting(double x, double y, Object pickTarget) {
        if (connectingEdge != null) {
            if (pickTarget instanceof TreeNode) {
                if (pickTarget != targetNode)
                    setTargetNode((TreeNode) pickTarget);
                connectingEdge.setEndPoint(targetCenter.getX(), targetCenter.getY());
                cutArrow(true);
            } else {
                connectingEdge.setEndPoint(topPane.sceneToLocal(x, y).getX(), topPane.sceneToLocal(x, y).getY());
                cutArrow(false);
            }
        }
    }

    void connectNodes(TreeNode parent, TreeNode child) {
        Set<TreeNode> subtree = child.getTree().removeSubtree(child);
        if (subtree != null) {
            parent.getTree().appendSubgraph(subtree, parent, child, -1);
        }
    }

    public void commitDragAndDropAction() {
        if (originNode != targetNode) {
            if (toggleGroup.getSelectedToggle() == ToggleConnecting
                    && originNode.getTree() != targetNode.getTree()) {
                connectNodes(this.originNode, this.targetNode);
            } else if (toggleGroup.getSelectedToggle() == ToggleMove) {
                originNode.getTree().swap(originNode, targetNode);
            }
        }
        topPane.getChildren().clear();
    }

    // Manage TreePanes
    public void removeTreePane(TreePane treePane) {
        if (mainTree.getValue() == treePane) {
            MainTreePane.getChildren().clear();
            if (!BoxUnassignedNodes.getChildren().isEmpty()) {
                mainTree.setValue((TreePane) BoxUnassignedNodes.getChildren().remove(0));
                MainTreePane.getChildren().add(mainTree.getValue());
            } else {
                mainTree.setValue(null);
            }
        } else {
            BoxUnassignedNodes.getChildren().remove(treePane);
        }
    }


    @FXML
    private void checkConstraints() {
        Tree tree = this.mainTree.getValue().getTree();

        this.vboxNotifications.getChildren().clear();


        // Check if mainTree consist of only an outcome
        if (tree.getRoot() instanceof Outcome) {
            this.vboxNotifications.getChildren().add(new NotificationBoxElement("Tree only consists of an outcome!", vboxNotifications));
        } else {
            // checks if all choice node have an informationset assigned
            boolean hasInformationSet = true;
            // checks if there aren't any node leaves
            boolean hasNoNodesAsLeaf = true;
            // write if two nodes of the same informationset are children of one another
            Set<InformationSet> informationsetsNotCanonical = new HashSet<>();
            // write if an informationset doesn't have a player assigned
            Set<InformationSet> informationsetsNoPlayer = new HashSet<>();
            // write if not all nodes in an informationset have same amount of actions
            Set<InformationSet> informationsetsNotEqualActions = new HashSet<>();

            Map<InformationSet, Set<TreeNode>> reachingNodes = new HashMap<>();
            Map<InformationSet, Integer> numOfChildren = new HashMap<>();
            for (TreeNode n : tree.getNodes()) {
                if (n instanceof ChoiceNode) {
                    InformationSet inf = ((ChoiceNode) n).getInformationSet();
                    if (inf != null) {
                        List<TreeNode> children = new LinkedList(((ChoiceNode) n).getGraphChildren());
                        int newValue = children.size();
                        Integer previousValue = numOfChildren.put(inf, newValue);

                        if (!reachingNodes.containsKey(inf))
                            reachingNodes.put(inf, new HashSet<>());

                        if (!informationsetsNotCanonical.contains(inf)) {
                            Set<TreeNode> reaching = reachingNodes.get(inf);
                            if (!reaching.contains(n)) {
                                reaching.add(n);
                                while (!children.isEmpty()) {
                                    TreeNode temp = children.remove(0);
                                    if (temp instanceof ChoiceNode)
                                        children.addAll(((ChoiceNode) temp).getGraphChildren());

                                    if (reaching.contains(temp))
                                        informationsetsNotCanonical.add(inf);

                                    reaching.add(temp);
                                }
                            } else {
                                informationsetsNotCanonical.add(inf);
                            }

                        }

                        if (newValue == 0)
                            hasNoNodesAsLeaf = false;
                        if (previousValue != null && newValue != previousValue)
                            informationsetsNotEqualActions.add(inf);
                        if (inf.getAssignedPlayer() == null)
                            informationsetsNoPlayer.add(inf);
                    } else {
                        hasInformationSet = false;
                    }
                }
            }

            if (hasInformationSet &&
                    hasNoNodesAsLeaf &&
                    informationsetsNotEqualActions.isEmpty() &&
                    informationsetsNoPlayer.isEmpty() && informationsetsNotCanonical.isEmpty()) {
                mainTree.getValue().setPassedCheck(true);
            }

            if (!hasInformationSet) {
                this.vboxNotifications.getChildren().add(new NotificationBoxElement("Not all nodes have an informationset assigned!", vboxNotifications));
            }
            if (!hasNoNodesAsLeaf) {
                this.vboxNotifications.getChildren().add(new NotificationBoxElement("Nodes cannot be leaves!", vboxNotifications));
            }
            if (!informationsetsNotEqualActions.isEmpty()) {
                String out = Arrays.toString(informationsetsNotEqualActions.toArray());
                this.vboxNotifications.getChildren().add(new NotificationBoxElement("The following informationsets don't have the same amount of actions: "
                        + out.substring(1, out.length() - 1) + ".", vboxNotifications));
            }
            if (!informationsetsNoPlayer.isEmpty()) {
                String out = Arrays.toString(informationsetsNoPlayer.toArray());
                this.vboxNotifications.getChildren().add(new NotificationBoxElement("The following informationsets don't have a player assigned: "
                        + out.substring(1, out.length() - 1) + ".", vboxNotifications));
            }
            if (!informationsetsNotCanonical.isEmpty()) {
                String out = Arrays.toString(informationsetsNotCanonical.toArray());
                this.vboxNotifications.getChildren().add(new NotificationBoxElement("The following informationsets have nodes which cover the same subtree (not canonical): "
                        + out.substring(1, out.length() - 1) + ".", vboxNotifications));
            }
        }
    }

    @FXML
    private void solve() {
        mainController.solve(mainTree.getValue().getTree());
        dataModel.cleanSelectedNodes();
    }

    private void setTextProperties(Text text) {
        text.setScaleX(1.5);
        text.setScaleY(1.5);
    }

    private void removeTree(Tree tree) {
        for (Node n : vboxForest.getChildren()) {
            TreePane treePane = (TreePane) n;
            if (treePane.getTree() == tree) {
                vboxForest.getChildren().remove(treePane);
                return;
            }
        }
    }

    // Initialize controller
    void init(MainController mainController, DataModel dataModel) {
        this.mainController = mainController;
        this.dataModel = dataModel;
        this.dataModel.getTrees().addListener((ListChangeListener<Tree>) change -> {
            while (change.next()) {
                change.getRemoved().forEach(this::removeTree);
                change.getAddedSubList().forEach(this::addTreeNode);
            }
        });

        this.BoxUnassignedNodes.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));

        this.stackPane.setOnMouseDragReleased(mouseDragEvent -> topPane.getChildren().clear());

        this.toggleGroup = new ToggleGroup();
        ToggleMove.setToggleGroup(toggleGroup);
        ToggleConnecting.setToggleGroup(toggleGroup);
        toggleConnecting();

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

        showPlayers = new SimpleBooleanProperty();
        showPlayers.bind(ShowPlayersCheckBox.selectedProperty());

        showOutcomes = new SimpleBooleanProperty();
        showOutcomes.bind(ShowOutcomesCheckBox.selectedProperty());

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

}
