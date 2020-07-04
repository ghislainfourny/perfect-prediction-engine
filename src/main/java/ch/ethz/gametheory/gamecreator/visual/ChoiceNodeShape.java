package ch.ethz.gametheory.gamecreator.visual;

import ch.ethz.gametheory.gamecreator.data.ChoiceNode;
import ch.ethz.gametheory.gamecreator.data.InformationSet;
import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.Player;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import javax.annotation.Nonnull;

public class ChoiceNodeShape extends TreeNodeShape<Circle, ChoiceNode> {

    private static final int NODE_SIZE = 30;

    private final ChangeListener<Player> playerChangeListener = (observableValue, oldPlayer, newPlayer) -> {
        getLabel().textProperty().unbind();
        if (newPlayer != null) {
            getLabel().textProperty().bind(newPlayer.nameProperty());
        }
    };

    public ChoiceNodeShape(@Nonnull Model model, @Nonnull ChoiceNode choiceNode) {
        this(new Circle(), new Label(), model, choiceNode);
    }

    private ChoiceNodeShape(@Nonnull Circle circle, @Nonnull Label playerText, @Nonnull Model model, ChoiceNode choiceNode) {
        super(model, choiceNode, circle, playerText);

        getLabel().setPickOnBounds(false);
        getLabel().setPrefWidth(Double.MAX_VALUE);
        getShape().radiusProperty().bind(model.scaleProperty().multiply(NODE_SIZE));

        getLabel().visibleProperty().bind(model.showPlayersProperty());
        getLabel().maxWidthProperty().bind(getShape().radiusProperty().multiply(2));
        getLabel().layoutXProperty().bind(getShape().radiusProperty().multiply(-1));
        getLabel().layoutYProperty().bind(getLabel().heightProperty().divide(-2));
        setFontColor();

        getData().informationSetProperty().addListener((observable, oldInformationSet, newInformationSet) -> {
            if (oldInformationSet != null) {
                cleanLabel(oldInformationSet);
            }
            cleanColor();
            if (newInformationSet != null) {
                bindNewInformationSetProperties(newInformationSet);
            }
        });
        if (getData().getInformationSet() != null) {
            bindNewInformationSetProperties(getData().getInformationSet());
        }
    }

    private void bindNewInformationSetProperties(InformationSet informationSet) {
        getShape().fillProperty().bind(informationSet.getColorProperty());
        informationSet.playerProperty().addListener(playerChangeListener);
        if (informationSet.getAssignedPlayer() != null) {
            getLabel().textProperty().bind(informationSet.getAssignedPlayer().nameProperty());
        }
    }

    private void cleanColor() {
        getShape().fillProperty().unbind();
        getShape().setFill(DEFAULT_FILL_COLOR);
    }

    private void cleanLabel(InformationSet informationSet) {
        ChangeListener<Player> playerChangeListener = (observableValue, oldPlayer, newPlayer) -> {
            getLabel().textProperty().unbind();
            getLabel().textProperty().bind(newPlayer.nameProperty());
        };
        informationSet.playerProperty().removeListener(playerChangeListener);
        getLabel().textProperty().unbind();
        getLabel().setText("");
    }

    private void setFontColor() {
        ObjectBinding<Paint> paintObjectBinding = new ObjectBinding<Paint>() {
            {
                super.bind(getShape().fillProperty());
            }

            @Override
            protected Paint computeValue() {
                Color color = (Color) getShape().getFill();
                double luminance = (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                int r;
                if (luminance > 0.5) {
                    r = 0;
                } else {
                    r = 255;
                }
                return Color.rgb(r, r, r);
            }
        };
        getLabel().textFillProperty().bind(paintObjectBinding);
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
