package ch.ethz.gametheory.gamecreator.controllers;
import ch.ethz.gametheory.gamecreator.*;

import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import java.util.HashSet;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;

public class SidePaneController {

    private final int ROW_HEIGHT = 48;

    private MainController mainController;

    private int numberOfPlayers = 1;
    private int numberOfInformationsets = 1;

    private static ObservableList<Player> playerList;
    private static ObservableList<InformationSet> informationsetList;

    private ListView<Player> ListViewOutcomes;
    private Set<Outcome> selectedOutcomes;

    // Objects defined in sidepane.fxml
    @FXML private ListView<Player> ListViewPlayers;
    @FXML private ListView<InformationSet> ListViewInformationsets;
    @FXML private AnchorPane AnchorPaneNodes;
    @FXML private AnchorPane AnchorPaneOutcomes;
    @FXML private TitledPane TPPlayers;
    @FXML private TitledPane TPInformationsets;
    @FXML private TitledPane TPNodes;
    @FXML private TitledPane TPOutcomes;

    public static ObservableList<Player> getPlayerList(){
        return playerList;
    }

    public static ObservableList<InformationSet> getInformationsetList() { return informationsetList; }

    // Adding nodes to forest pane
    protected ChoiceNode addNode(){
        ChoiceNode newChoiceNode = new ChoiceNode(mainController.scaleProperty());
        mainController.addTreeNode(newChoiceNode);
        return newChoiceNode;
    }

    protected Outcome addOutcome() {
        Outcome newOutcome = new Outcome(mainController.scaleProperty());
        mainController.addTreeNode(newOutcome);
        return newOutcome;
    }

    // Methods for adding/removing players/informationsets
    protected Player addPlayer(){
        Player newPlayer = new Player(numberOfPlayers++);
        playerList.add(newPlayer);
        return newPlayer;
    }

    private void removePlayer(Player player){
        player.setDeleted();
        playerList.remove(player);
    }

    protected InformationSet addInformationset(){
        InformationSet newInformationSet = new InformationSet(numberOfInformationsets++);
        informationsetList.add(newInformationSet);
        return newInformationSet;
    }

    private void removeInformationset(InformationSet informationSet){
        informationSet.setDeleted();
        informationsetList.remove(informationSet);
    }

    void updateSelectedNodes(Set<TreeNode> selectedNodes) {
        Set<ChoiceNode> nodes = new HashSet<>();
        Set<Outcome> outcomes = new HashSet<>();
        for (TreeNode s: selectedNodes) {
            if (s instanceof ChoiceNode){
                nodes.add((ChoiceNode) s);
            } else {
                outcomes.add((Outcome)s);
            }
        }
        setupNodesPanel(nodes);
        setupOutcomesPanel(outcomes);

    }

    private void setupNodesPanel(Set<ChoiceNode> choiceNodes) {
        AnchorPaneNodes.getChildren().clear();
        if (!choiceNodes.isEmpty()){
            ComboBox<InformationSet> informationSetComboBox = new ComboBox<>();
            informationSetComboBox.setItems(informationsetList);
            informationSetComboBox.setPromptText("Select Information Set");
            informationSetComboBox.setPlaceholder(new Text("No information sets available!"));
            InformationSet selected = null;
            boolean first = true;
            for (ChoiceNode n: choiceNodes) {
                if (first){
                    selected = n.getInformationSet();
                    first = false;
                } else if (selected != n.getInformationSet()){
                    selected = null;
                }
            }
            informationSetComboBox.setValue(selected);
            AnchorPaneNodes.getChildren().add(informationSetComboBox);
            setAnchors(informationSetComboBox);
            informationSetComboBox.valueProperty().addListener((observableValue, informationSet, t1) ->{
                choiceNodes.forEach(node -> node.setInformationSet(observableValue.getValue()));
                    });

        }
    }

    private void setupOutcomesPanel(Set<Outcome> outcomes) {
        selectedOutcomes = outcomes;
        AnchorPaneOutcomes.getChildren().clear();
        if (!outcomes.isEmpty()){
            this.ListViewOutcomes = new ListView();
            ListViewOutcomes.setCellFactory(playerListView -> new outcomeCell());
            ListViewOutcomes.setItems(playerList);
            ListViewOutcomes.setPrefHeight(0);
            ListViewOutcomes.setMinHeight(playerList.size() * ROW_HEIGHT + 2);
            setAnchors(ListViewOutcomes);
            AnchorPaneOutcomes.getChildren().add(ListViewOutcomes);
        } else {
          ListViewOutcomes = null;
        }
    }

    // Helper function
    private void addIconToButton(Button button){
        Text add = GlyphsDude.createIcon(FontAwesomeIcon.PLUS_SQUARE);
        add.setScaleX(2.0);
        add.setScaleY(2.0);
        add.setFill(Color.GREY);
        button.setGraphic(add);
    }

    private void setAnchors(javafx.scene.Node node){
        double padding = 5.0;
        AnchorPane.setTopAnchor(node, padding);
        AnchorPane.setBottomAnchor(node, padding);
        AnchorPane.setRightAnchor(node, padding);
        AnchorPane.setLeftAnchor(node, padding);
    }

    // Initialize controller
    void init(MainController mainController) {
        this.mainController = mainController;

        // Initialize observableList for players
        this.playerList = observableArrayList(
                e -> new Observable[] {e.nameProperty()});
        playerList.addListener((ListChangeListener<Player>) change -> {
            int height = playerList.size() * ROW_HEIGHT + 2;
            ListViewPlayers.setMinHeight(height);
            if(ListViewOutcomes!=null){
                ListViewOutcomes.setMinHeight(height);
            }
        });
        playerList.addListener((ListChangeListener<Player>) change -> ListViewPlayers.setMinHeight(playerList.size() * ROW_HEIGHT + 2));
        this.ListViewPlayers.setCellFactory(playerListView -> new playerCell());
        this.ListViewPlayers.setItems(playerList);

        // Initialize observableList for informationsets
        informationsetList = observableArrayList(
                e -> new Observable[] {e.playerProperty()});
        informationsetList.addListener((ListChangeListener<InformationSet>) change -> ListViewInformationsets.setMinHeight(informationsetList.size() * ROW_HEIGHT + 2));
        this.ListViewInformationsets.setCellFactory(informationSetListView -> new informationsetCell());
        this.ListViewInformationsets.setItems(informationsetList);

        Button ButtonAddPlayer = new Button();
        Button ButtonAddInformationset = new Button();
        Button ButtonAddNode = new Button();
        Button ButtonAddOutcome = new Button();

        // Add icons to predefined buttons
        setupTitledPanes(TPPlayers, "Players", ButtonAddPlayer);
        setupTitledPanes(TPInformationsets, "Information Set", ButtonAddInformationset);
        setupTitledPanes(TPNodes, "Selected Node(s)", ButtonAddNode);
        setupTitledPanes(TPOutcomes, "Selected Outcome(s)", ButtonAddOutcome);

        ButtonAddPlayer.setOnAction(action -> addPlayer());
        ButtonAddInformationset.setOnAction(action -> addInformationset());
        ButtonAddNode.setOnAction(action -> addNode());
        ButtonAddOutcome.setOnAction(action -> addOutcome());
    }

    private void setupTitledPanes(TitledPane tp, String lblText, Button button) {
        HBox contentBox = new HBox();
        contentBox.setAlignment(Pos.CENTER);
        contentBox.minWidthProperty().bind(tp.widthProperty().add(-40));

        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        pane.setMaxWidth(Double.MAX_VALUE);

        addIconToButton(button);

        contentBox.getChildren().addAll(
                new Label(lblText),
                pane,
                button
                );

        tp.setGraphic(contentBox);

    }

    // ListCell classes
    private class playerCell extends ListCell<Player> {

        private HBox hBox = new HBox();
        private Label label = new Label();
        private TextField textField = new TextField();

        playerCell() {
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

            button.setOnAction(event -> removePlayer(playerCell.this.getItem()));
            textField.textProperty().addListener((observableValue, s, t1) -> playerCell.this.getItem().setName(observableValue.getValue())
            );
        }

        @Override
        protected void updateItem(Player item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                label.setText(this.getItem().getId()+":");
                textField.setText(this.getItem().getName());
                setGraphic(hBox);
            }
        }
    }

    private class informationsetCell extends ListCell<InformationSet> {

        private HBox hBox = new HBox();
        private Label label = new Label();
        private ComboBox<Player> playerComboBox = new ComboBox<>();
        private ColorPicker colorPicker = new ColorPicker();

        informationsetCell (){
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
                    -> informationsetCell.this.getItem().setColor(observableValue.getValue()));

            Text text = GlyphsDude.createIcon(FontAwesomeIcon.TRASH_ALT);
            text.setFill(Color.WHITE);
            button.setGraphic(text);
            button.setId("button-negative");
            button.setOnAction(event -> removeInformationset(informationsetCell.this.getItem()));

            playerComboBox.setItems(playerList);
            playerComboBox.setPrefWidth(130);
            playerComboBox.setPlaceholder(new Text("No players available!"));
            playerComboBox.valueProperty().addListener((observableValue, o, t1) ->{
                        if (informationsetCell.this.getItem()!=null) {
                            informationsetCell.this.getItem().setAssignedPlayer(observableValue.getValue());
                        }
                    });
            playerComboBox.setButtonCell(new ListCell<Player>() {
                @Override
                protected void updateItem(Player item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null){
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
                label.setText(this.getItem().getId()+":");
                playerComboBox.setValue(this.getItem().getAssignedPlayer());
                colorPicker.setValue((Color) informationsetCell.this.getItem().getColor());
                setGraphic(hBox);
            }
        }
    }

    private class outcomeCell extends ListCell<Player> {

        private Label playerLbl;
        private TextField number;
        private HBox hBox;

        outcomeCell(){
            super();
            hBox = new HBox();
            playerLbl = new Label();
            number = new TextField();

            hBox.getChildren().addAll(playerLbl, number);
            hBox.setSpacing(10.0);
            hBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(number, Priority.ALWAYS);


            playerLbl.setStyle("-fx-font-weight: bold;");
            playerLbl.setMinWidth(100);

            number.setMaxWidth(Double.MAX_VALUE);
            number.textProperty().addListener((observableValue, s, t1) -> {
                if (t1.matches("^([+\\-])?\\d+$")){
                    int newPayoff;
                    try {
                        newPayoff = Integer.parseInt(t1);
                    } catch (Exception e) {
                        newPayoff = Integer.MAX_VALUE;
                    }
                    number.setText(""+newPayoff);
                    for (Outcome o: selectedOutcomes) {
                        o.setPlayerOutcome(outcomeCell.super.getItem(), newPayoff);
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
                playerLbl.setText(player+": ");
                int outcomeValue = 0;
                boolean hasChanged = false;
                boolean first = true;
                for (Outcome o: selectedOutcomes) {
                    int playerValue = o.getPlayerOutcome(player);
                    if (first){
                        outcomeValue = playerValue;
                        first = false;
                    } else if (outcomeValue != playerValue){
                        hasChanged = true;
                    }
                }
                number.setText(hasChanged?"":""+outcomeValue);
                setGraphic(hBox);
            }
        }
    }
}
