package ch.ethz.gametheory.gamecreator.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Random;

public class InformationSet {

    private ObjectProperty<Paint> color;
    private ObjectProperty<Player> assignedPlayer;
    private BooleanProperty deleted;
    private ChangeListener<Boolean> isDeletedListener;
    private final int id;

    public InformationSet(int id) {
        this.id = id;
        assignedPlayer = new SimpleObjectProperty<>(null);
        Random rand = new Random();
        color = new SimpleObjectProperty<>(Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
        deleted = new SimpleBooleanProperty(false);
        isDeletedListener = (observableValue, aBoolean, t1) -> setAssignedPlayer(null);
    }

    public int getId() {
        return id;
    }

    public ObjectProperty<Player> playerProperty() {
        return assignedPlayer;
    }

    public Player getAssignedPlayer() {
        return assignedPlayer.getValue();
    }

    public void setAssignedPlayer(Player assignedPlayer) {
        if (assignedPlayer != this.assignedPlayer.getValue()) {
            if (assignedPlayer != this.assignedPlayer.getValue() && this.getAssignedPlayer() != null) {
                this.getAssignedPlayer().deletedProperty().removeListener(isDeletedListener);
            }
            this.assignedPlayer.setValue(assignedPlayer);

            if (this.assignedPlayer.getValue() != null) {
                this.getAssignedPlayer().deletedProperty().addListener(isDeletedListener);
            }
        }
    }

    public void setColor(Color assignedColor) {
        this.color.set(assignedColor);
    }

    public Paint getColor() {
        return color.get();
    }

    public ObjectProperty<Paint> getColorProperty() {
        return color;
    }

    public BooleanProperty deletedProperty() {
        return deleted;
    }

    public void setDeleted() {
        this.deleted.set(true);
    }

    @Override
    public String toString() {
        return "Information Set " + id
                + (assignedPlayer.getValue() == null ? "" : "; " +
                (assignedPlayer.getValue().getName().isEmpty() ? "Player " + assignedPlayer.getValue().getId() : assignedPlayer.getValue().getName()));
    }


}
