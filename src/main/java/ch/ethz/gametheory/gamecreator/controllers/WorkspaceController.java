package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.DragAndDropOperation;
import ch.ethz.gametheory.gamecreator.TreeValidator;
import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.visual.*;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;

public class WorkspaceController {

    private Arrow connectingEdge;
    private Model model;
    private ToggleGroup toggleGroup;

    // Objects defined in workspace.fxml
    @FXML
    private ForestController forestController;

    @FXML
    private Button buttonRotateAnticlockwise;
    @FXML
    private Button buttonRotateClockwise;
    @FXML
    private ToggleButton toggleToolConnect;
    @FXML
    private ToggleButton toggleToolSwap;
    @FXML
    private CheckBox checkBoxShowPlayers;
    @FXML
    private CheckBox checkBoxShowPayouts;
    @FXML
    private Button btnCheckConstraints;
    @FXML
    private Button btnSolve;

    @FXML
    private StackPane stackPane;
    @FXML
    private Pane topPane;
    @FXML
    private NotificationBox notificationBox;

    @FXML
    void rotateClockwise() {
        forestController.rotateClockwise();
    }

    @FXML
    void rotateAnticlockwise() {
        forestController.rotateAnticlockwise();
    }

    @FXML
    private void toggleConnecting() {
        toggleGroup.selectToggle(toggleToolConnect);
    }

    @FXML
    private void toggleMove() {
        toggleGroup.selectToggle(toggleToolSwap);
    }

    private void startConnecting(@Nonnull TreeNodeShape<?, ?> shape) {
        if (toggleGroup.getSelectedToggle() != toggleToolConnect || shape instanceof ChoiceNodeShape) {

            Point2D originCenter = topPane.sceneToLocal(shape.localToScene(shape.getCenter()));

            if (toggleGroup.getSelectedToggle() == toggleToolConnect) {
                connectingEdge = new ConnectArrow();
            } else if (toggleGroup.getSelectedToggle() == toggleToolSwap) {
                connectingEdge = new SwapArrow();
            }

            connectingEdge.setStartPoint(originCenter.getX(), originCenter.getY());
            topPane.getChildren().add(connectingEdge);
        }
    }

    public void updateConnecting(double x, double y) {
        TreeNodeShape<?, ?> originNodeShape = model.getOriginNodeShape();
        if (originNodeShape != null && connectingEdge != null) {
            TreeNodeShape<?, ?> targetNodeShape = model.getTargetNodeShape();
            if (targetNodeShape == null) {
                connectingEdge.setEndPoint(x, y);
            }
            connectingEdge.cutArrow(originNodeShape, targetNodeShape);
        }
    }

    @FXML
    private void checkConstraints() {
        this.notificationBox.clearNotifications();
        TreeValidator treeValidator = new TreeValidator(forestController.getMainTree());
        String message = treeValidator.checkConstraints();
        forestController.mainTreeProperty().get().setPassedCheck(message.isEmpty());
        if (!message.isEmpty()) {
            notificationBox.addNotification(message);
        }
    }

    @FXML
    private void solve() {
        model.cleanSelectedNodes();
        TreeValidator treeValidator = new TreeValidator(forestController.getMainTree());
        treeValidator.checkConstraints();
        treeValidator.solve();
    }

    private void setTextProperties(Text text) {
        text.setScaleX(1.5);
        text.setScaleY(1.5);
    }


    // Initialize controller
    void init(Model model) {
        this.model = model;
        forestController.init(model);
        setUpDragAndDrop(model);
        model.showPlayersProperty().bind(checkBoxShowPlayers.selectedProperty());
        model.showPayoutsProperty().bind(checkBoxShowPayouts.selectedProperty());
        setUpSolvePanel();
        setIcons();
    }

    private void setUpSolvePanel() {
        this.btnCheckConstraints.setDisable(true);
        this.btnSolve.disableProperty().bind(btnCheckConstraints.disableProperty().not().or(forestController.mainTreeProperty().isNull()));

        forestController.mainTreeProperty().addListener(((observableValue, treePane, t1) -> {
            if (observableValue.getValue() == null) {
                btnCheckConstraints.disableProperty().unbind();
                btnCheckConstraints.setDisable(true);
            } else {
                btnCheckConstraints.disableProperty().bind(observableValue.getValue().passedCheckProperty());
            }
        }));
    }

    private void setUpDragAndDrop(Model model) {
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
                    if (observable.getValue() != null) {
                        updateConnecting(0, 0);
                    }
                }
        );

        initializeDragAndDropToggle();
    }

    private void initializeDragAndDropToggle() {
        this.toggleGroup = new ToggleGroup();
        toggleToolSwap.setToggleGroup(toggleGroup);
        toggleToolConnect.setToggleGroup(toggleGroup);
        toggleConnecting();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == toggleToolConnect) {
                model.setDragAndDropOperation(DragAndDropOperation.CONNECT);
            } else if (newValue == toggleToolSwap) {
                model.setDragAndDropOperation(DragAndDropOperation.SWAP);
            }
        });
    }

    private void setIcons() {
        Text tempText = GlyphsDude.createIcon(FontAwesomeIcon.SHARE);
        setTextProperties(tempText);
        buttonRotateClockwise.setGraphic(tempText);
        tempText = GlyphsDude.createIcon(FontAwesomeIcon.REPLY);
        setTextProperties(tempText);
        buttonRotateAnticlockwise.setGraphic(tempText);
        tempText = GlyphsDude.createIcon(FontAwesomeIcon.EXTERNAL_LINK_SQUARE);
        setTextProperties(tempText);
        toggleToolConnect.setGraphic(tempText);
        tempText = GlyphsDude.createIcon(FontAwesomeIcon.ARROWS_ALT);
        setTextProperties(tempText);
        toggleToolSwap.setGraphic(tempText);
    }

}
