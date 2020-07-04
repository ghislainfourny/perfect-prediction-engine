package ch.ethz.gametheory.gamecreator.visual;

import javafx.scene.layout.VBox;

import java.util.Collection;

public class NotificationBox extends VBox {

    public void addNotifications(Collection<String> messages) {
        for (String message : messages) {
            addNotification(message);
        }
    }

    public void addNotification(String message) {
        getChildren().add(new NotificationBoxElement(message, this));
    }

    public void clearNotifications() {
        getChildren().clear();
    }

}
