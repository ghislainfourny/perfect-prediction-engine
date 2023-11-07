package ch.ethz.gametheory.gamecreator;

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
    private BooleanProperty isDeleted;
    private ChangeListener<Boolean> isDeletedListener;
    private final int id;

    public InformationSet(int id) {
        this.id = id;
        assignedPlayer = new SimpleObjectProperty<>(null);
        Random rand = new Random();
        color = new SimpleObjectProperty<>(Color.rgb(rand.nextInt(256), rand.nextInt(256),rand.nextInt(256)));
        isDeleted = new SimpleBooleanProperty(false);
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
        if (assignedPlayer!=this.assignedPlayer.getValue()) {
            if (assignedPlayer != this.assignedPlayer.getValue() && this.getAssignedPlayer() != null) {
                this.getAssignedPlayer().isDeletedProperty().removeListener(isDeletedListener);
            }
            this.assignedPlayer.setValue(assignedPlayer);

            if (this.assignedPlayer.getValue() != null) {
                this.getAssignedPlayer().isDeletedProperty().addListener(isDeletedListener);
            }
        }
    }

    public void setColor(Color assignedColor) {
        this.color.set(assignedColor);
    }

    public Paint getColor() {
        return color.get();
    }

    ObjectProperty<Paint> getColorProperty(){
        return color;
    }

    BooleanProperty isDeletedProperty(){
        return isDeleted;
    }

    public void setDeleted() {
        this.isDeleted.set(true);
    }

    @Override
    public String toString() {
        return "Informationset " + id
                + (assignedPlayer.getValue()==null?"":"; " +
                (assignedPlayer.getValue().getName().isEmpty()?"Player " + assignedPlayer.getValue().getId():assignedPlayer.getValue().getName()));
    }


}
