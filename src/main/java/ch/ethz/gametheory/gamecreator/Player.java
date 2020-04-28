package ch.ethz.gametheory.gamecreator;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Player {

    private final int id;
    private StringProperty name;
    private BooleanProperty isDeleted;

    public Player(int id) {
        this.id = id;
        name = new SimpleStringProperty("");
        isDeleted = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        if (!getName().equals(name.trim())){
            this.name.set(name.trim());
        }
    }

    public String getName() {
        return name.get();
    }

    BooleanProperty isDeletedProperty() {
        return isDeleted;
    }

    public void setDeleted(){
        isDeleted.set(true);
    }

    @Override
    public String toString() {
        return getName().isEmpty()?"Player " + id:getName();
    }
}
