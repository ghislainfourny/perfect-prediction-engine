package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.data.DataModel;
import ch.ethz.gametheory.gamecreator.data.Player;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PlayerCell extends ListCell<Player> {

    private HBox hBox = new HBox();
    private Label label = new Label();
    private TextField textField = new TextField();

    public PlayerCell(DataModel dataModel) {
        super();
        Button button = new Button();
        Pane pane = new Pane();
        pane.setPrefWidth(30);

        hBox.getChildren().addAll(label, textField, pane, button);
        hBox.setSpacing(10.0);
        HBox.setHgrow(textField, Priority.ALWAYS);
        hBox.setAlignment(Pos.CENTER_LEFT);

        label.setStyle("-fx-font-weight: bold;");
        label.setMinWidth(25);

        textField.setPromptText("Optional Name");

        Text text = GlyphsDude.createIcon(FontAwesomeIcon.TRASH_ALT);
        text.setFill(Color.WHITE);
        button.setGraphic(text);
        button.setId("button-negative");

        button.setOnAction(event -> dataModel.removePlayer(PlayerCell.this.getItem()));
        textField.textProperty().addListener((observableValue, s, t1) -> PlayerCell.this.getItem().setName(observableValue.getValue())
        );
    }

    @Override
    protected void updateItem(Player item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            label.setText(this.getItem().getId() + ":");
            textField.setText(this.getItem().getName());
            setGraphic(hBox);
        }
    }
}