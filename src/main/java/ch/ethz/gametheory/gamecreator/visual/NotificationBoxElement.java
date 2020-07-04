package ch.ethz.gametheory.gamecreator.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class NotificationBoxElement extends HBox {

    public NotificationBoxElement(String information, VBox notificationBox) {
        Node informationNode = getInformationNode(information);
        Node closeNode = getCloseNode(notificationBox);
        setBoxStyle();
        this.getChildren().addAll(informationNode, closeNode);
    }

    private void setBoxStyle() {
        this.setStyle("-fx-background-color: -swatch-400;");
        this.setPadding(new Insets(5, 10, 5, 10));
        this.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
    }

    private Node getInformationNode(String information) {
        Label informationLabel = new Label(information);
        informationLabel.setTextFill(Color.WHITE);
        informationLabel.getStyleClass().remove("label");
        informationLabel.setMaxWidth(Double.MAX_VALUE);
        informationLabel.setAlignment(Pos.CENTER);
        HBox.setHgrow(informationLabel, Priority.ALWAYS);
        return informationLabel;
    }

    private Node getCloseNode(VBox notificationBox) {
        Label closeObject = new Label("x");
        closeObject.getStyleClass().remove("label");
        closeObject.setAlignment(Pos.CENTER_RIGHT);
        closeObject.setFont(Font.font(18));
        closeObject.setOnMouseClicked(n -> {
            notificationBox.getChildren().remove(this);
        });
        closeObject.setOnMouseEntered(mouseEvent -> closeObject.setTextFill(Color.WHITE));
        closeObject.setOnMouseExited(mouseEvent -> closeObject.setTextFill(Color.BLACK));
        return closeObject;
    }

}
