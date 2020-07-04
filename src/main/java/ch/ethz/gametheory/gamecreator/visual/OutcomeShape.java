package ch.ethz.gametheory.gamecreator.visual;

import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.Outcome;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class OutcomeShape extends TreeNodeShape<Rectangle, Outcome> {

    private static final int OUTCOME_HEIGHT = 60;
    private static final int OUTCOME_WIDTH = 60;

    private double minWidth = 0.0;
    private final DoubleProperty centerXProperty;
    private final DoubleProperty centerYProperty;

    public OutcomeShape(Model model, Outcome outcome) {
        this(new Rectangle(), new Label(), model, outcome);
    }

    private OutcomeShape(Rectangle rectangle, Label playerOutcomes, Model model, Outcome outcome) {
        super(model, outcome, rectangle, playerOutcomes);
        this.centerXProperty = new SimpleDoubleProperty();
        this.centerYProperty = new SimpleDoubleProperty();

        getShape().widthProperty().bind(playerOutcomes.widthProperty());
        getShape().heightProperty().bind(playerOutcomes.heightProperty());

        this.centerXProperty.bind(rectangle.widthProperty().divide(2).add(rectangle.getX()));
        this.centerYProperty.bind(rectangle.heightProperty().divide(2).add(rectangle.getY()));

        getLabel().minWidthProperty().bind(model.scaleProperty().multiply(OUTCOME_WIDTH));
        getLabel().minHeightProperty().bind(model.scaleProperty().multiply(OUTCOME_HEIGHT));
        getLabel().visibleProperty().bind(model.showPayoutsProperty());
        getLabel().visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (observableValue.getValue()) {
                playerOutcomes.textProperty().bind(getData().getOutputText());
            } else {
                playerOutcomes.textProperty().unbind();
                playerOutcomes.setText("");
                setMinWidth(false);
            }
        });
        if (playerOutcomes.isVisible()) playerOutcomes.textProperty().bind(getData().getOutputText());

        getData().solutionProperty().addListener((observableValue, aBoolean, t1) -> {
            setSelected(false);
            getShape().setStroke(observableValue.getValue() ? SOLUTION_STROKE_COLOR : DEFAULT_STROKE_COLOR);
        });
    }

    public double getCenterX() {
        return centerXProperty.getValue();
    }

    public double getCenterY() {
        return centerYProperty.getValue();
    }

    private void setMinWidth(boolean isShown) {
        if (isShown) {
            Text t = new Text(getLabel().getText());
            t.setFont(getLabel().getFont());
            double INSET = 5.0;
            double width = t.getBoundsInLocal().getWidth() + 2 * INSET;
            getLabel().setPrefWidth(Double.max(this.minWidth, width));
        } else {
            getLabel().setPrefWidth(this.minWidth);
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
    public double centerToBorderDistance(double angle) {
        double width = getShape().getWidth();
        double height = getShape().getHeight();
        if (height / width > angle) {
            return Math.sqrt(Math.pow(width / 2, 2) + Math.pow(width / 2 * angle, 2));
        } else {
            return Math.sqrt(Math.pow(height / 2, 2) + Math.pow(width / 2 / angle, 2));
        }
    }

    @Override
    public void setSelected(boolean value) {
        getShape().setStroke(value ? SELECTED_STROKE_COLOR : DEFAULT_STROKE_COLOR);
    }

}
