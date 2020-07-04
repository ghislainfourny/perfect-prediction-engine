package ch.ethz.gametheory.gamecreator.visual;

import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.Outcome;
import ch.ethz.gametheory.gamecreator.data.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class OutcomeCell extends ListCell<Player> {

    private final Label playerLbl;
    private final TextField number;
    private final HBox hBox;
    private final Model model;

    public OutcomeCell(Model model) {
        super();
        hBox = new HBox();
        playerLbl = new Label();
        number = new TextField();
        this.model = model;

        hBox.getChildren().addAll(playerLbl, number);
        hBox.setSpacing(10.0);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(number, Priority.ALWAYS);


        playerLbl.setStyle("-fx-font-weight: bold;");
        playerLbl.setMinWidth(100);

        number.setMaxWidth(Double.MAX_VALUE);
        number.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.matches("^([+\\-])?\\d+$")) {
                int newPayoff;
                try {
                    newPayoff = Integer.parseInt(t1);
                } catch (Exception e) {
                    newPayoff = Integer.MAX_VALUE;
                }
                number.setText("" + newPayoff);
                for (Outcome o : model.getSelectedOutcomes()) {
                    o.setPlayerOutcome(OutcomeCell.super.getItem(), newPayoff);
                }
            }
        });

    }

    @Override
    protected void updateItem(Player item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            Player player = this.getItem();
            playerLbl.setText(player + ": ");
            int outcomeValue = 0;
            boolean hasChanged = false;
            boolean first = true;
            for (Outcome o : model.getSelectedOutcomes()) {
                int playerValue = o.getPlayerOutcome(player);
                if (first) {
                    outcomeValue = playerValue;
                    first = false;
                } else if (outcomeValue != playerValue) {
                    hasChanged = true;
                }
            }
            number.setText(hasChanged ? "" : "" + outcomeValue);
            setGraphic(hBox);
        }
    }

}
