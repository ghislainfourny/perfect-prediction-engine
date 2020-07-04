package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.data.*;
import ch.ethz.gametheory.gamecreator.visual.InformationSetCell;
import ch.ethz.gametheory.gamecreator.visual.OutcomeCell;
import ch.ethz.gametheory.gamecreator.visual.PlayerCell;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SidePaneController {

    private final int ROW_HEIGHT = 48;

    private ListView<Player> ListViewOutcomes;
    private ComboBox<InformationSet> informationSetOfSelectedChoiceNodeComboBox;
    private Model model;

    // Objects defined in sidepane.fxml
    @FXML
    private ListView<Player> ListViewPlayers;
    @FXML
    private ListView<InformationSet> ListViewInformationSets;
    @FXML
    private AnchorPane containerChoiceNodesPanel;
    @FXML
    private AnchorPane AnchorPaneOutcomes;
    @FXML
    private TitledPane TPPlayers;
    @FXML
    private TitledPane TPInformationSets;
    @FXML
    private TitledPane TPNodes;
    @FXML
    private TitledPane TPOutcomes;

    private void addPlayer() {
        model.newPlayer();
    }

    private void addInformationSet() {
        model.newInformationSet();
    }

    private void addChoiceNode() {
        model.newChoiceNode();
    }

    private void addOutcome() {
        model.newOutcome();
    }

    private void refreshSelectedChoiceNodesPanel() {
        InformationSet selected = null;
        boolean first = true;
        for (ChoiceNode n : model.getSelectedChoiceNodes()) {
            if (first) {
                selected = n.getInformationSet();
                first = false;
            } else if (selected != n.getInformationSet()) {
                selected = null;
                break;
            }
        }
        informationSetOfSelectedChoiceNodeComboBox.setValue(selected);
        informationSetOfSelectedChoiceNodeComboBox.setDisable(model.getSelectedChoiceNodes().isEmpty());
    }

    private void refreshSelectedOutcomesPanel() {
        ListViewOutcomes.refresh();
        ListViewOutcomes.setDisable(model.getSelectedOutcomes().isEmpty());
    }

    private void setupChoiceNodesPanel() {
        informationSetOfSelectedChoiceNodeComboBox = new ComboBox<>();
        informationSetOfSelectedChoiceNodeComboBox.setItems(model.getInformationSets());
        informationSetOfSelectedChoiceNodeComboBox.setPromptText("Select Information Set");
        informationSetOfSelectedChoiceNodeComboBox.setPlaceholder(new Text("No information sets available!"));
        informationSetOfSelectedChoiceNodeComboBox.valueProperty().addListener((observableValue, informationSet, t1) -> {
            if (observableValue.getValue() != null) {
                model.getSelectedChoiceNodes().forEach(node -> node.setInformationSet(observableValue.getValue()));
            }
        });

        containerChoiceNodesPanel.getChildren().add(informationSetOfSelectedChoiceNodeComboBox);
        setAnchors(informationSetOfSelectedChoiceNodeComboBox);
    }

    private void setupOutcomesPanel() {
        ListViewOutcomes = new ListView<>();
        ListViewOutcomes.setCellFactory(playerListView -> new OutcomeCell(model));
        ListViewOutcomes.setPrefHeight(0);
        ListViewOutcomes.setMinHeight(model.getPlayers().size() * ROW_HEIGHT + 2);
        ListViewOutcomes.setItems(model.getPlayers());
        ListViewOutcomes.setDisable(true);

        setAnchors(ListViewOutcomes);
        AnchorPaneOutcomes.getChildren().add(ListViewOutcomes);
    }


    // Initialize controller
    void init(Model model) {
        this.model = model;
        this.model.getSelectedChoiceNodes().addListener((SetChangeListener<ChoiceNode>) change -> refreshSelectedChoiceNodesPanel());
        this.model.getSelectedOutcomes().addListener((SetChangeListener<Outcome>) change -> refreshSelectedOutcomesPanel());

        // Initialize observableList for players
        ObservableList<Player> playerList = model.getPlayers();
        playerList.addListener((ListChangeListener<Player>) change -> {
            int height = playerList.size() * ROW_HEIGHT + 2;
            ListViewPlayers.setMinHeight(height);
            if (ListViewOutcomes != null) {
                ListViewOutcomes.setMinHeight(height);
            }
        });
        playerList.addListener((ListChangeListener<Player>) change -> ListViewPlayers.setMinHeight(playerList.size() * ROW_HEIGHT + 2));
        this.ListViewPlayers.setCellFactory(playerListView -> new PlayerCell(model));
        this.ListViewPlayers.setItems(playerList);

        // Initialize observableList for information sets
        ObservableList<InformationSet> informationSetList = model.getInformationSets();
        informationSetList.addListener((ListChangeListener<InformationSet>) change -> ListViewInformationSets.setMinHeight(informationSetList.size() * ROW_HEIGHT + 2));
        this.ListViewInformationSets.setCellFactory(informationSetListView -> new InformationSetCell(model));
        this.ListViewInformationSets.setItems(informationSetList);

        Button ButtonAddPlayer = new Button();
        Button ButtonAddInformationSet = new Button();
        Button ButtonAddNode = new Button();
        Button ButtonAddOutcome = new Button();

        // Add icons to predefined buttons
        setupTitledPanes(TPPlayers, "Players", ButtonAddPlayer);
        setupTitledPanes(TPInformationSets, "Information Set", ButtonAddInformationSet);
        setupTitledPanes(TPNodes, "Selected Node(s)", ButtonAddNode);
        setupTitledPanes(TPOutcomes, "Selected Outcome(s)", ButtonAddOutcome);

        ButtonAddPlayer.setOnAction(action -> addPlayer());
        ButtonAddInformationSet.setOnAction(action -> addInformationSet());
        ButtonAddNode.setOnAction(action -> addChoiceNode());
        ButtonAddOutcome.setOnAction(action -> addOutcome());

        setupChoiceNodesPanel();
        setupOutcomesPanel();
    }

    // Helper function
    private static void addIconToButton(Button button) {
        Text add = GlyphsDude.createIcon(FontAwesomeIcon.PLUS_SQUARE);
        add.setScaleX(2.0);
        add.setScaleY(2.0);
        add.setFill(Color.GREY);
        button.setGraphic(add);
    }

    private static void setAnchors(javafx.scene.Node node) {
        double padding = 5.0;
        AnchorPane.setTopAnchor(node, padding);
        AnchorPane.setBottomAnchor(node, padding);
        AnchorPane.setRightAnchor(node, padding);
        AnchorPane.setLeftAnchor(node, padding);
    }

    private static void setupTitledPanes(TitledPane tp, String text, Button button) {
        HBox contentBox = new HBox();
        contentBox.setAlignment(Pos.CENTER);
        contentBox.minWidthProperty().bind(tp.widthProperty().add(-40));

        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        pane.setMaxWidth(Double.MAX_VALUE);

        addIconToButton(button);

        contentBox.getChildren().addAll(new Label(text), pane, button);

        tp.setGraphic(contentBox);

    }
}
