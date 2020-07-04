package ch.ethz.gametheory.gamecreator.visual;

import ch.ethz.gametheory.gamecreator.data.ChoiceNode;
import ch.ethz.gametheory.gamecreator.data.Model;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import javax.annotation.Nonnull;

public class ChoiceNodeShape extends TreeNodeShape<Circle, ChoiceNode> {

    private static final int NODE_SIZE = 30;

    public ChoiceNodeShape(@Nonnull Model model, @Nonnull ChoiceNode choiceNode) {
        this(new Circle(), new Label(), model, choiceNode);
    }

    private ChoiceNodeShape(@Nonnull Circle circle, @Nonnull Label playerText, @Nonnull Model model, ChoiceNode choiceNode) {
        super(model, choiceNode, circle, playerText);

        ChangeListener<Paint> textColorListener = (observableValue, paint, t1) -> setFontColor((Color) observableValue.getValue());

        getLabel().setPickOnBounds(false);
        getLabel().setPrefWidth(Double.MAX_VALUE);
        getShape().radiusProperty().bind(model.scaleProperty().multiply(NODE_SIZE));

        getLabel().visibleProperty().bind(model.showPlayersProperty());
        getLabel().setTextFill(Color.BLACK);
        getLabel().maxWidthProperty().bind(getShape().radiusProperty().multiply(2));
        getLabel().layoutXProperty().bind(getShape().radiusProperty().multiply(-1));
        getLabel().layoutYProperty().bind(getLabel().heightProperty().divide(-2));

        getShape().fillProperty().addListener(textColorListener);
        setDefaultColor();
    }

    private void setFontColor(Color color) {
        double luminance = (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
        int r;
        if (luminance > 0.5) {
            r = 0;
        } else {
            r = 255;
        }
        getLabel().setTextFill(Color.rgb(r, r, r));
    }

    private void setDefaultColor() {
        getShape().setFill(DEFAULT_FILL_COLOR);
    }

    public double getCenterX() {
        return getShape().getCenterX();
    }

    public double getCenterY() {
        return getShape().getCenterY();
    }

    @Override
    public DoubleProperty centerXProperty() {
        return getShape().centerXProperty();
    }

    @Override
    public DoubleProperty centerYProperty() {
        return getShape().centerYProperty();
    }

    @Override
    public double centerToBorderDistance(double angle) {
        return getShape().getRadius();
    }


}
