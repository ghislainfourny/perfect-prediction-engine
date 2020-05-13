package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.data.DataModel;
import ch.ethz.gametheory.gamecreator.data.InformationSet;
import ch.ethz.gametheory.gamecreator.data.Player;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class InformationSetCell extends ListCell<InformationSet> {

    private HBox hBox = new HBox();
    private Label label = new Label();
    private ComboBox<Player> playerComboBox = new ComboBox<>();
    private ColorPicker colorPicker = new ColorPicker();

    public InformationSetCell(DataModel dataModel) {
        super();
        Button button = new Button();
        Pane pane = new Pane();
        pane.setPrefWidth(5.0);

        hBox.getChildren().addAll(label, playerComboBox, colorPicker, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        hBox.setSpacing(10.0);
        hBox.setAlignment(Pos.CENTER_LEFT);

        label.setStyle("-fx-font-weight: bold;");
        label.setMinWidth(25);

        String promptText = "Select Player";
        playerComboBox.setPromptText(promptText);

        colorPicker.valueProperty().addListener((observableValue, color, t1)
                -> InformationSetCell.this.getItem().setColor(observableValue.getValue()));

        Text text = GlyphsDude.createIcon(FontAwesomeIcon.TRASH_ALT);
        text.setFill(Color.WHITE);
        button.setGraphic(text);
        button.setId("button-negative");
        button.setOnAction(event -> dataModel.removeInformationSet(InformationSetCell.this.getItem()));

        playerComboBox.setItems(dataModel.getPlayers());
        playerComboBox.setPrefWidth(130);
        playerComboBox.setPlaceholder(new Text("No players available!"));
        playerComboBox.valueProperty().addListener((observableValue, o, t1) -> {
            if (InformationSetCell.this.getItem() != null) {
                InformationSetCell.this.getItem().setAssignedPlayer(observableValue.getValue());
            }
        });
        playerComboBox.setButtonCell(new ListCell<Player>() {
            @Override
            protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(promptText);
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    @Override
    protected void updateItem(InformationSet item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            label.setText(this.getItem().getId() + ":");
            playerComboBox.setValue(this.getItem().getAssignedPlayer());
            colorPicker.setValue((Color) InformationSetCell.this.getItem().getColor());
            setGraphic(hBox);
        }
    }
}