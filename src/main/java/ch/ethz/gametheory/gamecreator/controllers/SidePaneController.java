package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.*;
import ch.ethz.gametheory.gamecreator.data.DataModel;
import ch.ethz.gametheory.gamecreator.data.InformationSet;
import ch.ethz.gametheory.gamecreator.data.Player;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.Set;

public class SidePaneController {

    private final int ROW_HEIGHT = 48;

    private ListView<Player> ListViewOutcomes;
    private DataModel dataModel;
    private MainController mainController;

    // Objects defined in sidepane.fxml
    @FXML
    private ListView<Player> ListViewPlayers;
    @FXML
    private ListView<InformationSet> ListViewInformationSets;
    @FXML
    private AnchorPane AnchorPaneNodes;
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
        dataModel.addPlayer();
    }

    private void addInformationSet() {
        dataModel.addInformationSet();
    }

    private void addChoiceNode() {
        dataModel.addChoiceNode();
    }

    private void addOutcome() {
        dataModel.addOutcome();
    }

    void updateSelectedNodes(Set<TreeNode> selectedNodes) {
        Set<ChoiceNode> nodes = new HashSet<>();
        Set<Outcome> outcomes = new HashSet<>();
        for (TreeNode s : selectedNodes) {
            if (s instanceof ChoiceNode) {
                nodes.add((ChoiceNode) s);
            } else {
                outcomes.add((Outcome) s);
            }
        }
        setupNodesPanel(nodes);
        setupOutcomesPanel(outcomes);

    }

    private void setupNodesPanel(Set<ChoiceNode> choiceNodes) {
        AnchorPaneNodes.getChildren().clear();
        if (!choiceNodes.isEmpty()) {
            ComboBox<InformationSet> informationSetComboBox = new ComboBox<>();
            informationSetComboBox.setItems(dataModel.getInformationSets());
            informationSetComboBox.setPromptText("Select Information Set");
            informationSetComboBox.setPlaceholder(new Text("No information sets available!"));
            InformationSet selected = null;
            boolean first = true;
            for (ChoiceNode n : choiceNodes) {
                if (first) {
                    selected = n.getInformationSet();
                    first = false;
                } else if (selected != n.getInformationSet()) {
                    selected = null;
                }
            }
            informationSetComboBox.setValue(selected);
            AnchorPaneNodes.getChildren().add(informationSetComboBox);
            setAnchors(informationSetComboBox);
            informationSetComboBox.valueProperty().addListener((observableValue, informationSet, t1) -> {
                choiceNodes.forEach(node -> node.setInformationSet(observableValue.getValue()));
            });

        }
    }

    private void setupOutcomesPanel(Set<Outcome> outcomes) {
        AnchorPaneOutcomes.getChildren().clear();
        if (!outcomes.isEmpty()) {
            this.ListViewOutcomes = new ListView();
            ListViewOutcomes.setCellFactory(playerListView -> new OutcomeCell(dataModel));
            ListViewOutcomes.setItems(dataModel.getPlayers());
            ListViewOutcomes.setPrefHeight(0);
            ListViewOutcomes.setMinHeight(dataModel.getPlayers().size() * ROW_HEIGHT + 2);
            setAnchors(ListViewOutcomes);
            AnchorPaneOutcomes.getChildren().add(ListViewOutcomes);
        } else {
            ListViewOutcomes = null;
        }
    }

    // Helper function
    private void addIconToButton(Button button) {
        Text add = GlyphsDude.createIcon(FontAwesomeIcon.PLUS_SQUARE);
        add.setScaleX(2.0);
        add.setScaleY(2.0);
        add.setFill(Color.GREY);
        button.setGraphic(add);
    }

    private void setAnchors(javafx.scene.Node node) {
        double padding = 5.0;
        AnchorPane.setTopAnchor(node, padding);
        AnchorPane.setBottomAnchor(node, padding);
        AnchorPane.setRightAnchor(node, padding);
        AnchorPane.setLeftAnchor(node, padding);
    }

    // Initialize controller
    void init(MainController mainController, DataModel dataModel) {
        this.mainController = mainController;
        this.dataModel = dataModel;

        // Initialize observableList for players
        ObservableList<Player> playerList = dataModel.getPlayers();
        playerList.addListener((ListChangeListener<Player>) change -> {
            int height = playerList.size() * ROW_HEIGHT + 2;
            ListViewPlayers.setMinHeight(height);
            if (ListViewOutcomes != null) {
                ListViewOutcomes.setMinHeight(height);
            }
        });
        playerList.addListener((ListChangeListener<Player>) change -> ListViewPlayers.setMinHeight(playerList.size() * ROW_HEIGHT + 2));
        this.ListViewPlayers.setCellFactory(playerListView -> new PlayerCell(dataModel));
        this.ListViewPlayers.setItems(playerList);

        // Initialize observableList for informationsets

        ObservableList<InformationSet> informationSetList = dataModel.getInformationSets();
        informationSetList.addListener((ListChangeListener<InformationSet>) change -> ListViewInformationSets.setMinHeight(informationSetList.size() * ROW_HEIGHT + 2));
        this.ListViewInformationSets.setCellFactory(informationSetListView -> new InformationSetCell(dataModel));
        this.ListViewInformationSets.setItems(informationSetList);

        Button ButtonAddPlayer = new Button();
        Button ButtonAddInformationset = new Button();
        Button ButtonAddNode = new Button();
        Button ButtonAddOutcome = new Button();

        // Add icons to predefined buttons
        setupTitledPanes(TPPlayers, "Players", ButtonAddPlayer);
        setupTitledPanes(TPInformationSets, "Information Set", ButtonAddInformationset);
        setupTitledPanes(TPNodes, "Selected Node(s)", ButtonAddNode);
        setupTitledPanes(TPOutcomes, "Selected Outcome(s)", ButtonAddOutcome);

        ButtonAddPlayer.setOnAction(action -> addPlayer());
        ButtonAddInformationset.setOnAction(action -> addInformationSet());
        ButtonAddNode.setOnAction(action -> addChoiceNode());
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
}
