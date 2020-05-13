package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.controllers.ForestController;
import ch.ethz.gametheory.gamecreator.data.DataModel;
import ch.ethz.gametheory.gamecreator.data.Player;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class Outcome extends TreeNode {

    private static final int OUTCOME_HEIGHT = 60;
    private static final int OUTCOME_WIDTH = 60;
    private static final Color selectedColor = Color.RED;
    private Color normalColor;

    private Map<Player, IntegerProperty> outcomeNumbers;
    private Rectangle rectangle;
    private Label playerOutcomes;
    private double minWidth = 0.0;
    private DoubleProperty centerXProperty;
    private DoubleProperty centerYProperty;
    private ObservableList<Player> playerList;

    public Outcome(DataModel dataModel) {
        this(new Rectangle(), new Label(), dataModel);
    }

    private Outcome(Rectangle rectangle, Label playerOutcomes, DataModel dataModel) {
        super(rectangle, playerOutcomes);
        setEvents(dataModel);
        this.playerList = dataModel.getPlayers();
        this.playerList.addListener((ListChangeListener<? super Player>) change -> {
            if (playerOutcomes.isVisible()) setOutcomesText();
        });
        this.outcomeNumbers = new HashMap<>();
        this.centerXProperty = new SimpleDoubleProperty();
        this.centerYProperty = new SimpleDoubleProperty();
        this.normalColor = Color.BLACK;

        this.rectangle = rectangle;
        this.rectangle.setStrokeWidth(2.0);
        this.rectangle.setFill(Color.LIGHTGREY);
        this.rectangle.setStroke(normalColor);
        this.rectangle.widthProperty().bind(playerOutcomes.widthProperty());
        this.rectangle.heightProperty().bind(playerOutcomes.heightProperty());

        this.centerXProperty.bind(rectangle.widthProperty().divide(2).add(rectangle.getX()));
        this.centerYProperty.bind(rectangle.heightProperty().divide(2).add(rectangle.getY()));

        this.playerOutcomes = playerOutcomes;
        this.playerOutcomes.getStyleClass().remove("label");
        this.playerOutcomes.minWidthProperty().bind(dataModel.scaleProperty().multiply(OUTCOME_WIDTH));
        this.playerOutcomes.minHeightProperty().bind(dataModel.scaleProperty().multiply(OUTCOME_HEIGHT));
        this.playerOutcomes.setAlignment(Pos.CENTER);
        this.playerOutcomes.visibleProperty().bind(ForestController.showOutcomesProperty());
        this.playerOutcomes.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue()) {
                setOutcomesText();
            } else {
                playerOutcomes.setText("");
                setMinWidth(false);
            }
        });
        if (playerOutcomes.isVisible()) setOutcomesText();

        this.solutionProperty().addListener((observableValue, aBoolean, t1) -> {
            this.normalColor = observableValue.getValue() ? Color.GOLD : Color.BLACK;
            setSelected(false);
        });
    }

    private void setOutcomesText() {
        if (!this.playerList.isEmpty()) {
            StringBuilder s = new StringBuilder();
            for (Player p : playerList) {
                s.append(getPlayerOutcome(p)).append(", ");
            }
            s.setLength(s.length() - 2);
            playerOutcomes.setText(s.toString());
            setMinWidth(true);
        } else {
            playerOutcomes.setText("");
            setMinWidth(false);
        }
    }

    private IntegerProperty getPlayerOutcomeProperty(Player player) {
        if (!outcomeNumbers.containsKey(player)) {
            IntegerProperty t = new SimpleIntegerProperty(0);
            t.addListener(((observableValue, number, t1) -> {
                if (playerOutcomes.isVisible()) setOutcomesText();
                toggleChanged();
            }));
            toggleChanged();
            outcomeNumbers.put(player, t);
        }
        return outcomeNumbers.get(player);
    }

    public int getPlayerOutcome(Player player) {
        return getPlayerOutcomeProperty(player).get();
    }

    public void setPlayerOutcome(Player player, int value) {
        getPlayerOutcomeProperty(player).setValue(value);
    }

    public double getCenterX() {
        return centerXProperty.getValue();
    }

    public double getCenterY() {
        return centerYProperty.getValue();
    }

    public double getRectangleHeight() {
        return this.rectangle.getHeight();
    }

    public double getRectangleWidth() {
        return this.rectangle.getWidth();
    }

    private void setMinWidth(boolean isShown) {
        if (isShown) {
            Text t = new Text(playerOutcomes.getText());
            t.setFont(playerOutcomes.getFont());
            double INSET = 5.0;
            double width = t.getBoundsInLocal().getWidth() + 2 * INSET;
            playerOutcomes.setPrefWidth(Double.max(this.minWidth, width));
        } else {
            playerOutcomes.setPrefWidth(this.minWidth);
        }
    }

    @Override
    public DoubleProperty centerXProperty() {
        return centerXProperty;
    }

    @Override
    public DoubleProperty centerYProperty() {
        return centerYProperty;
    }

    @Override
    public void setSelected(boolean value) {
        this.rectangle.setStroke(value ? selectedColor : normalColor);
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
