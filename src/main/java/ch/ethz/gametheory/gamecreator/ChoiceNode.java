package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.controllers.ForestController;
import ch.ethz.gametheory.gamecreator.data.DataModel;
import ch.ethz.gametheory.gamecreator.data.InformationSet;
import ch.ethz.gametheory.gamecreator.data.Player;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class ChoiceNode extends TreeNode {

    private static final int NODE_SIZE = 30;
    private static final Color selectedColor = Color.RED;
    private Color normalColor;

    private InformationSet informationSet;
    private ChangeListener<Boolean> deletedListener;
    private ChangeListener<Player> playerListener;
    private Circle circle;
    private Label playerText;
    private List<TreeNode> children;
    private BooleanProperty deleted;

    public ChoiceNode(DataModel dataModel) {
        this(new Circle(), new Label(), dataModel);
    }

    private ChoiceNode(Circle circle, Label playerText, DataModel dataModel) {
        super(circle, playerText);
        setEvents(dataModel);
        this.circle = circle;
        this.playerText = playerText;
        this.children = new ArrayList<>();
        this.deletedListener = (observableValue, paint, t1) -> {
            setInformationSet(null);
            toggleChanged();
        };
        this.playerListener = (observableValue, paint, t1) -> {
            this.playerText.textProperty().unbind();
            this.playerText.setText("");
            if (observableValue.getValue() != null) {
                this.playerText.textProperty().bind(observableValue.getValue().nameProperty());
            }
            toggleChanged();
        };
        ChangeListener<Paint> textColorListener = (observableValue, paint, t1) -> setFontColor((Color) observableValue.getValue());
        this.normalColor = Color.BLACK;

        this.playerText.setPickOnBounds(false);
        this.playerText.setPrefWidth(Double.MAX_VALUE);
        this.playerText.setAlignment(Pos.CENTER);
        this.circle.setStrokeWidth(2.0);
        this.circle.setStroke(normalColor);
        this.circle.radiusProperty().bind(dataModel.scaleProperty().multiply(NODE_SIZE));

        this.playerText.visibleProperty().bind(ForestController.showPlayersProperty());
        this.playerText.setTextFill(Color.BLACK);
        this.playerText.getStyleClass().remove("label");
        this.playerText.maxWidthProperty().bind(this.circle.radiusProperty().multiply(2));
        this.playerText.layoutXProperty().bind(this.circle.radiusProperty().multiply(-1));
        this.playerText.layoutYProperty().bind(this.playerText.heightProperty().divide(-2));

        this.circle.fillProperty().addListener(textColorListener);
        setDefaultColor();
    }

    /**
     * @return list of copy of this node's children
     */
    public List<TreeNode> getGraphChildren() {
        return this.children;
    }

    void addChild(TreeNode node, int pos) {
        if (-1 < pos && pos <= this.children.size())
            children.add(pos, node);
        else
            this.children.add(node);
        toggleChanged();
    }

    int removeChild(TreeNode node) {
        int index = this.children.indexOf(node);
        boolean removed = this.children.remove(node);
        if (removed) {
            toggleChanged();
        }
        return index;
    }

    private void setFontColor(Color color) {
        double luminance = (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
        int r;
        if (luminance > 0.5)
            r = 0;
        else
            r = 255;
        this.playerText.setTextFill(Color.rgb(r, r, r));
    }

    private void setDefaultColor() {
        this.circle.setFill(Color.LIGHTGREY);
    }

    public InformationSet getInformationSet() {
        return informationSet;
    }

    private void cleanInformationset() {
        this.informationSet.deletedProperty().removeListener(deletedListener);
        this.informationSet.playerProperty().removeListener(playerListener);
        this.informationSet.getColorProperty().unbind();
        this.playerText.textProperty().unbind();
        this.playerText.textFillProperty().unbind();
        this.playerText.setText("");
        this.circle.fillProperty().unbind();
    }

    public void setInformationSet(InformationSet informationSet) {
        if (this.informationSet != informationSet) {
            toggleChanged();
            if (this.informationSet != null)
                cleanInformationset();

            this.informationSet = informationSet;

            if (informationSet != null) {
                this.informationSet.deletedProperty().addListener(deletedListener);
                this.informationSet.playerProperty().addListener(playerListener);
                this.circle.fillProperty().bind(informationSet.getColorProperty());
                if (this.informationSet.getAssignedPlayer() != null)
                    this.playerText.textProperty().bind(this.informationSet.getAssignedPlayer().nameProperty());
            } else
                setDefaultColor();
        }
    }

    public double getRadius() {
        return this.circle.getRadius();
    }

    public double getCenterX() {
        return this.circle.getCenterX();
    }

    public double getCenterY() {
        return this.circle.getCenterY();
    }

    @Override
    public DoubleProperty centerXProperty() {
        return this.circle.centerXProperty();
    }

    @Override
    public DoubleProperty centerYProperty() {
        return this.circle.centerYProperty();
    }

    @Override
    public void setSelected(boolean value) {
        this.circle.setStroke(value ? selectedColor : normalColor);
    }

    @Override
    public BooleanProperty deletedProperty() {
        return null;
    }

    private void setEvents(DataModel dataModel) {
        // Selecting
        this.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.isControlDown()) {
                dataModel.toggleSelectedNode(this);
            } else {
                dataModel.setSelectedNode(this);
            }
            mouseEvent.consume();
        });
    }

}
