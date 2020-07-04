package ch.ethz.gametheory.gamecreator.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Player {

    private final int id;
    private final StringProperty name;
    private final BooleanProperty deleted;

    public Player(int id) {
        this.id = id;
        name = new SimpleStringProperty("");
        deleted = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        if (!getName().equals(name.trim())) {
            this.name.set(name.trim());
        }
    }

    public String getName() {
        return name.get();
    }

    public BooleanProperty deletedProperty() {
        return deleted;
    }

    public void setDeleted() {
        deleted.set(true);
    }

    @Override
    public String toString() {
        return getName().isEmpty() ? "Player " + id : getName();
    }
}
