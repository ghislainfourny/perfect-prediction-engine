package ch.ethz.gametheory.gamecreator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class NotificationBox extends HBox {

    public NotificationBox(String information, VBox notificationBox){
        Label label = new Label(information);
        label.setTextFill(Color.WHITE);
        label.getStyleClass().remove("label");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        Label close = new Label("x");
        close.getStyleClass().remove("label");
        close.setAlignment(Pos.CENTER_RIGHT);
        close.setOnMouseClicked( n -> {notificationBox.getChildren().remove(this);});
        this.setStyle("-fx-background-color: -swatch-400;");
        this.setPadding(new Insets(5,10,5,10));
        this.setBorder(new Border(new BorderStroke(Color.WHITE,BorderStrokeStyle.SOLID, CornerRadii.EMPTY,new BorderWidths(1))));
        HBox.setHgrow(label, Priority.ALWAYS);
        this.getChildren().addAll(label, close);

    }
}
