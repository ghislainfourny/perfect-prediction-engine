package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.GameDataWrapper;
import ch.ethz.gametheory.gamecreator.data.*;
import ch.ethz.gametheory.gamecreator.visual.TreePane;
import ch.ethz.gametheory.gamecreator.xmlhelper.ChoiceNodeXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.InformationSetXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.PlayerXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.TreeXML;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainController implements Initializable {

    @FXML
    private SidePaneController sidePaneController;
    @FXML
    private ForestController forestController;
    @FXML
    private MenuController menuController;

    @FXML
    private Label lblZoom;
    @FXML
    private Slider sliderZoom;
    @FXML
    private TabPane mainTabPane;

    private Stage stage;
    private Model model;

    public void setStage(Stage stage) {
        this.stage = stage;
        getStage().getScene().setOnKeyPressed(e -> {
            if (!e.isControlDown()) {
                switch (e.getCode()) {
                    case DELETE:
                        model.deleteSelectedNodes();
                        break;
                    default:
                }
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    void saveState(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(GameDataWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            GameDataWrapper wrapper = new GameDataWrapper();

            List<PlayerXML> players = new LinkedList<>();
            model.getPlayers().forEach(p -> players.add(PlayerXML.convert(p)));
            if (!players.isEmpty()) {
                wrapper.setPlayers(players);
            }

            List<InformationSetXML> informationSets = new LinkedList<>();
            model.getInformationSets().forEach(i -> informationSets.add(InformationSetXML.convert(i)));
            if (!informationSets.isEmpty()) {
                wrapper.setInformationSets(informationSets);
            }

            Map<TreeNode, Integer> uniqueIdentifier = new HashMap<>();
            AtomicInteger counter = new AtomicInteger();
            Tree mainTree = this.forestController.getMainTree();
            if (mainTree != null) {
                TreeXML mainTreeXML = TreeXML.convert(mainTree, uniqueIdentifier, counter, model);
                wrapper.setMainTree(mainTreeXML);
            }

            List<TreeXML> components = new LinkedList<>();
            this.model.getTrees().forEach(tree -> components.add(TreeXML.convert(tree, uniqueIdentifier, counter, model)));
            if (!components.isEmpty()) {
                wrapper.setComponents(components);
            }

            m.marshal(wrapper, file);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data to file:\n" + file.getPath());
            alert.showAndWait();
        }
    }

    private void loadTreeHelper(TreeXML treeXML, Map<Integer,
            Player> playerLookup, Map<Integer, InformationSet> informationSetLookup, Map<Integer, TreeNode> nodeLookup) {
        if (treeXML.choiceNodes != null) {
            treeXML.choiceNodes.forEach(n -> {
                ChoiceNode newNode = model.newChoiceNode();
                if (n.informationsetID >= 0) newNode.setInformationSet(informationSetLookup.get(n.informationsetID));
                nodeLookup.put(n.nodeID, newNode);
            });
        }
        if (treeXML.outcomes != null) {
            treeXML.outcomes.forEach(o -> {
                Outcome newOutcome = model.newOutcome();
                o.payouts.forEach((p, v) -> newOutcome.setPlayerOutcome(playerLookup.get(p), v));
                nodeLookup.put(o.nodeID, newOutcome);
            });
        }
        if (treeXML.choiceNodes != null) {
            for (ChoiceNodeXML n : treeXML.choiceNodes) {
                ChoiceNode parentNode = (ChoiceNode) nodeLookup.get(n.nodeID);
                TreePane parentPane = this.forestController.getTreePane(parentNode);
                if (n.children != null) {
                    for (int child : n.children) {
                        this.model.connectNodes(parentNode, nodeLookup.get(child));
                    }
                }

            }
        }

    }

    public void loadFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(GameDataWrapper.class);
            Unmarshaller um = context.createUnmarshaller();
            GameDataWrapper wrapper = (GameDataWrapper) um.unmarshal(file);


            Map<Integer, Player> playerLookup = new HashMap<>();
            if (wrapper.getPlayers() != null) {
                wrapper.getPlayers().forEach(p -> {
                    Player newPlayer = model.newPlayer();
                    newPlayer.setName(p.name);
                    playerLookup.put(p.ID, newPlayer);
                });
            }

            Map<Integer, InformationSet> informationSetLookup = new HashMap<>();
            if (wrapper.getInformationSets() != null) {
                wrapper.getInformationSets().forEach(i -> {
                    InformationSet newInformationSet = model.newInformationSet();
                    if (i.playerID >= 0) newInformationSet.setAssignedPlayer(playerLookup.get(i.playerID));
                    newInformationSet.setColor(Color.valueOf(i.color));
                    informationSetLookup.put(i.ID, newInformationSet);
                });
            }

            Map<Integer, TreeNode> nodeLookup = new HashMap<>();
            if (wrapper.getMainTree() != null) {
                loadTreeHelper(wrapper.getMainTree(), playerLookup, informationSetLookup, nodeLookup);
                if (wrapper.getComponents() != null) {
                    wrapper.getComponents().forEach(t -> {
                        if (t != null) {
                            loadTreeHelper(t, playerLookup, informationSetLookup, nodeLookup);
                        }
                    });
                }
            }

            menuController.setMostRecentSavedFile(file);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data from file:\n" + file.getPath());
            System.err.println(Arrays.toString(e.getStackTrace()));
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        model = new Model();

        model.scaleProperty().bind(sliderZoom.valueProperty());
        lblZoom.textProperty().bind(model.scaleProperty().multiply(100).asString("%.0f").concat("%"));

        sidePaneController.init(model);
        forestController.init(model);
        menuController.init(this);
    }


}
