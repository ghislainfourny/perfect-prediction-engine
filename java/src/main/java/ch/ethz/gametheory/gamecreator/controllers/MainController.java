package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.*;
import ch.ethz.gametheory.gamecreator.xmlhelper.ChoiceNodeXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.InformationSetXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.PlayerXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.TreeXML;
import ch.ethz.gametheory.ptesolver.GameFactory;
import ch.ethz.gametheory.ptesolver.GameWithImperfectInformation;
import ch.ethz.gametheory.ptesolver.NaiveGameFactory;
import ch.ethz.gametheory.ptesolver.PTESolver;
import javafx.beans.property.DoubleProperty;
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

    @FXML private SidePaneController sidePaneController;
    @FXML private ForestController forestController;
    @FXML private MenuController menuController;

    @FXML private Label lblZoom;
    @FXML private Slider sliderZoom;
    @FXML private TabPane mainTabPane;

    private DoubleProperty scaleProperty;

    private Stage stage;

    // From sidepanel to forest
    void addTreeNode(TreeNode root){
        forestController.addTreeNode(root);
    }

    // From forest to sidepanel
    void updateSelectedNodes(Set<TreeNode> selectedNodes) {
        sidePaneController.updateSelectedNodes(selectedNodes);
    }

    public DoubleProperty scaleProperty(){
        return scaleProperty;
    }

    void solve(Tree mainTree) {
        List<Integer> choiceNodeToInformationset = new LinkedList<>();
        List<Integer> informationsetToPlayer = new LinkedList<>();

        Map<Player, Integer> playerToNum = new HashMap<>();
        Map<InformationSet, Integer> informationsetToNum = new HashMap<>();
        Map<TreeNode, Integer> shapeToNum = new HashMap<>();

        int numOfChoiceNodes = 0;
        int numOfInformationsets = 0;
        int numOfPlayers = 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(mainTree.getRoot());
        while (!queue.isEmpty()){
            TreeNode parent = queue.poll();
            if (parent instanceof ChoiceNode) {
                queue.addAll(((ChoiceNode) parent).getGraphChildren());
                shapeToNum.put(parent, numOfChoiceNodes++);
                InformationSet temp = ((ChoiceNode) parent).getInformationSet();
                if (!informationsetToNum.containsKey(temp)){
                    informationsetToNum.put(temp, numOfInformationsets++);
                    if (!playerToNum.containsKey(temp.getAssignedPlayer())){
                        playerToNum.put(temp.getAssignedPlayer(), numOfPlayers++);
                    }
                    informationsetToPlayer.add(playerToNum.get(temp.getAssignedPlayer()));
                }
                choiceNodeToInformationset.add(informationsetToNum.get(temp));
            }
        }

        List<Integer[]> outcomes = new LinkedList<>();
        List<Integer[]> partialActions = new LinkedList<>();
        Map<InformationSet, Integer> firstInformationsetAction = new HashMap<>();

        int numOfActions = 0;

        queue.add(mainTree.getRoot());
        while (!queue.isEmpty()){
            TreeNode parent = queue.poll();
            if (parent instanceof ChoiceNode){
                List<TreeNode> children = ((ChoiceNode) parent).getGraphChildren();
                queue.addAll(children);
                int tempActionNum;

                if (firstInformationsetAction.containsKey(((ChoiceNode) parent).getInformationSet())){
                    tempActionNum = firstInformationsetAction.get(((ChoiceNode) parent).getInformationSet());
                } else {
                    firstInformationsetAction.put( ((ChoiceNode) parent).getInformationSet(), numOfActions);
                    tempActionNum = numOfActions;
                }

                for (TreeNode child: children) {
                    if (child instanceof Outcome)
                        shapeToNum.put(child, numOfChoiceNodes++);
                    partialActions.add(new Integer[]{tempActionNum++, shapeToNum.get(parent), shapeToNum.get(child)});
                }

                if (tempActionNum>numOfActions)
                    numOfActions=tempActionNum;

            } else {
                Integer[] outcome = new Integer[numOfPlayers];
                for (Player p: playerToNum.keySet()) {
                    outcome[playerToNum.get(p)] = ((Outcome)parent).getPlayerOutcome(p);
                }
                outcomes.add(outcome);
            }
        }

        int[] primitiveChoiceNodeToInformationset = new int[choiceNodeToInformationset.size()];
        for (int i = 0; i < primitiveChoiceNodeToInformationset.length; i++)
            primitiveChoiceNodeToInformationset[i]=choiceNodeToInformationset.get(i);
        int[] primitiveInformationsetToPlayer = new int[numOfInformationsets];
        for (int i = 0; i < primitiveInformationsetToPlayer.length; i++)
            primitiveInformationsetToPlayer[i]=informationsetToPlayer.get(i);
        int[][] primitivePartialActions = new int[partialActions.size()][3];
        for (int i = 0; i < primitivePartialActions.length; i++){
            Integer[] temp = partialActions.get(i);
            primitivePartialActions[i][0]=temp[0];
            primitivePartialActions[i][1]=temp[1];
            primitivePartialActions[i][2]=temp[2];
        }

        Integer[][] primitiveOutcomes = new Integer[outcomes.size()][numOfPlayers];
        for (int i = 0; i < primitiveOutcomes.length; i++)
                for (int j = 0; j < primitiveOutcomes[i].length; j++)
                    primitiveOutcomes[i][j] = outcomes.get(i)[j];

        GameFactory<Integer> gameFactory = new NaiveGameFactory<>(Integer.class);
        try {
            gameFactory.parseData(
                    primitiveChoiceNodeToInformationset,
                    primitiveInformationsetToPlayer,
                    primitivePartialActions,
                    primitiveOutcomes
            );
            GameWithImperfectInformation<Integer> game = gameFactory.createGame();
            PTESolver<Integer> gameSolver = new PTESolver<>(game, Integer.class);
            Integer[][] solutions = gameSolver.solve().getOutcome();
            Player[] players = new Player[playerToNum.size()];
            playerToNum.forEach((player, integer) -> players[integer] = player);
            mainTree.setSolution(solutions, players);
        } catch (Exception e){
            System.err.println("Something went terribly wrong!");
        }


    }

    public void setStage(Stage stage){
        this.stage = stage;
        getStage().getScene().setOnKeyPressed(e -> {
            if (!e.isControlDown()) {
                switch (e.getCode()){
                    case DELETE: forestController.deleteSelected(); break;
                }
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    void saveState(File file){
        try {
            JAXBContext context = JAXBContext.newInstance(GameDataWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            GameDataWrapper wrapper = new GameDataWrapper();

            List<PlayerXML> players = new LinkedList<>();
            SidePaneController.getPlayerList().forEach(p -> players.add(PlayerXML.convert(p)));
            if (!players.isEmpty()){
                wrapper.setPlayers(players);
            }

            List<InformationSetXML> informationSets = new LinkedList<>();
            SidePaneController.getInformationsetList().forEach(i -> informationSets.add(InformationSetXML.convert(i)));
            if (!informationSets.isEmpty()){
                wrapper.setInformationSets(informationSets);
            }

            Map<TreeNode, Integer> uniqueIdentifier = new HashMap<>();
            AtomicInteger counter = new AtomicInteger();
            Tree mainTree = this.forestController.getMainTree();
            if (mainTree != null){
                TreeXML mainTreeXML = TreeXML.convert(mainTree, uniqueIdentifier, counter);
                wrapper.setMainTree(mainTreeXML);
            }

            List<TreeXML> components = new LinkedList<>();
            this.forestController.getComponents().forEach(tree -> components.add(TreeXML.convert(tree, uniqueIdentifier, counter)));
            if (!components.isEmpty()){
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
            Player> playerLookup, Map<Integer, InformationSet> informationSetLookup, Map<Integer, TreeNode> nodeLookup){
        if (treeXML.choiceNodes != null) {
            treeXML.choiceNodes.forEach(n -> {
                ChoiceNode newNode = this.sidePaneController.addNode();
                if (n.informationsetID >= 0) newNode.setInformationSet(informationSetLookup.get(n.informationsetID));
                nodeLookup.put(n.nodeID, newNode);
            });
        }
        if (treeXML.outcomes != null) {
            treeXML.outcomes.forEach(o -> {
                Outcome newOutcome = this.sidePaneController.addOutcome();
                o.payouts.forEach((p, v) -> newOutcome.setPlayerOutcome(playerLookup.get(p), v));
                nodeLookup.put(o.nodeID, newOutcome);
            });
        }
        if (treeXML.choiceNodes != null) {
            for (ChoiceNodeXML n: treeXML.choiceNodes) {
                ChoiceNode parentNode = (ChoiceNode) nodeLookup.get(n.nodeID);
                TreePane parentPane = this.forestController.getTreePane(parentNode);
                if (n.children !=null){
                    for (int child: n.children) {
                        this.forestController.connectNodes(parentNode, nodeLookup.get(child));
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
                    Player newPlayer = this.sidePaneController.addPlayer();
                    newPlayer.setName(p.name);
                    playerLookup.put(p.ID, newPlayer);
                });
            }

            Map<Integer, InformationSet> informationSetLookup = new HashMap<>();
            if (wrapper.getInformationSets() != null) {
                wrapper.getInformationSets().forEach(i -> {
                    InformationSet newInformationSet = this.sidePaneController.addInformationset();
                    if (i.playerID >= 0) newInformationSet.setAssignedPlayer(playerLookup.get(i.playerID));
                    newInformationSet.setColor(Color.valueOf(i.color));
                    informationSetLookup.put(i.ID, newInformationSet);
                });
            }

            Map<Integer, TreeNode> nodeLookup = new HashMap<>();
            if (wrapper.getMainTree() != null){
                loadTreeHelper(wrapper.getMainTree(), playerLookup, informationSetLookup, nodeLookup);
                if (wrapper.getComponents() != null){
                    wrapper.getComponents().forEach(t -> {if (t != null) {
                        loadTreeHelper(t, playerLookup, informationSetLookup, nodeLookup);}
                    });
                }
            }

            menuController.setMostRecentSavedFile(file);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data from file:\n" + file.getPath());
            System.err.println(Arrays.toString(e.getStackTrace()));
            alert.showAndWait();
        }
    }

    @FXML public void initialize(URL url, ResourceBundle resourceBundle) {



        scaleProperty = sliderZoom.valueProperty();
        lblZoom.textProperty().bind(scaleProperty().multiply(100).asString("%.0f").concat("%"));

        sidePaneController.init(this);
        forestController.init(this);
        menuController.init(this);



    }



}
