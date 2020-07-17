package ch.ethz.gametheory.gamecreator.data;

import ch.ethz.gametheory.gamecreator.GameDataWrapper;
import ch.ethz.gametheory.gamecreator.jsonhelper.InformationSetJson;
import ch.ethz.gametheory.gamecreator.jsonhelper.PlayerJson;
import ch.ethz.gametheory.gamecreator.jsonhelper.TreeJson;
import ch.ethz.gametheory.gamecreator.xmlhelper.ChoiceNodeXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.InformationSetXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.PlayerXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.TreeXML;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ModelIO {

    private final Model model;

    private File mostRecentSavedFile;

    public ModelIO(Model model) {
        this.model = model;
    }

    public File getMostRecentSavedFile() {
        return mostRecentSavedFile;
    }

    public void setMostRecentSavedFile(File mostRecentSavedFile) {
        this.mostRecentSavedFile = mostRecentSavedFile;
    }

    public void saveAsJson(File file) {
        try {
            Gson gson = new GsonBuilder().create();
            Map<String, List<?>> objectObjectMap = new HashMap<>();
            List<PlayerJson> players = model.getPlayers().stream().map(PlayerJson::convert).collect(Collectors.toList());
            List<InformationSetJson> informationSets = model.getInformationSets().stream().map(InformationSetJson::convert).collect(Collectors.toList());
            List<TreeJson> forest = new LinkedList<>();
            Map<TreeNode, Integer> uniqueIdentifier = new HashMap<>();
            AtomicInteger counter = new AtomicInteger();
            this.model.getTrees().forEach(tree -> forest.add(TreeJson.convert(tree, uniqueIdentifier, counter, model)));

            objectObjectMap.put("players", players);
            objectObjectMap.put("informationSets", informationSets);
            objectObjectMap.put("forest", forest);
            String s = gson.toJson(objectObjectMap);

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(s);
            fileWriter.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data to file:\n" + file.getPath());
            alert.showAndWait();
        }
    }

    public void saveAsXml(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(GameDataWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            GameDataWrapper wrapper = new GameDataWrapper();

            savePlayers(wrapper);
            saveInformationSet(wrapper);

            Map<TreeNode, Integer> uniqueIdentifier = new HashMap<>();
            AtomicInteger counter = new AtomicInteger();

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

    private void saveInformationSet(GameDataWrapper wrapper) {
        List<InformationSetXML> informationSets = new LinkedList<>();
        model.getInformationSets().forEach(i -> informationSets.add(InformationSetXML.convert(i)));
        if (!informationSets.isEmpty()) {
            wrapper.setInformationSets(informationSets);
        }
    }

    private void savePlayers(GameDataWrapper wrapper) {
        List<PlayerXML> players = new LinkedList<>();
        model.getPlayers().forEach(p -> players.add(PlayerXML.convert(p)));
        if (!players.isEmpty()) {
            wrapper.setPlayers(players);
        }
    }

    private void loadTreeHelper(TreeXML treeXML, Map<Integer,
            Player> playerLookup, Map<Integer, InformationSet> informationSetLookup, Map<Integer, TreeNode> nodeLookup) {
        if (treeXML.choiceNodes != null) {
            treeXML.choiceNodes.forEach(n -> {
                ChoiceNode newNode = model.newChoiceNode();
                if (n.informationsetID >= 0) {
                    newNode.setInformationSet(informationSetLookup.get(n.informationsetID));
                }
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

            Map<Integer, Player> playerLookup = loadPlayers(wrapper);
            Map<Integer, InformationSet> informationSetLookup = loadInformationSets(wrapper, playerLookup);
            loadForest(wrapper, playerLookup, informationSetLookup);

            setMostRecentSavedFile(file);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data from file:\n" + file.getPath());
            System.err.println(Arrays.toString(e.getStackTrace()));
            alert.showAndWait();
        }
    }

    private void loadForest(GameDataWrapper wrapper, Map<Integer, Player> playerLookup, Map<Integer, InformationSet> informationSetLookup) {
        Map<Integer, TreeNode> nodeLookup = new HashMap<>();
        if (wrapper.getComponents() != null) {
            wrapper.getComponents().forEach(t -> {
                if (t != null) {
                    loadTreeHelper(t, playerLookup, informationSetLookup, nodeLookup);
                }
            });
        }
    }

    private Map<Integer, InformationSet> loadInformationSets(GameDataWrapper wrapper, Map<Integer, Player> playerLookup) {
        Map<Integer, InformationSet> informationSetLookup = new HashMap<>();
        if (wrapper.getInformationSets() != null) {
            wrapper.getInformationSets().forEach(i -> {
                InformationSet newInformationSet = model.newInformationSet();
                if (i.playerID >= 0) newInformationSet.setAssignedPlayer(playerLookup.get(i.playerID));
                newInformationSet.setColor(Color.valueOf(i.color));
                informationSetLookup.put(i.ID, newInformationSet);
            });
        }
        return informationSetLookup;
    }

    private Map<Integer, Player> loadPlayers(GameDataWrapper wrapper) {
        Map<Integer, Player> playerLookup = new HashMap<>();
        if (wrapper.getPlayers() != null) {
            wrapper.getPlayers().forEach(p -> {
                Player newPlayer = model.newPlayer();
                newPlayer.setName(p.name);
                playerLookup.put(p.ID, newPlayer);
            });
        }
        return playerLookup;
    }

}
