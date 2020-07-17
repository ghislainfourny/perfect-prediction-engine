package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.GUI;
import ch.ethz.gametheory.gamecreator.data.ModelIO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MenuController {

    private Stage stage;
    private ModelIO modelIO;

    @FXML
    public void saveAsWorkspace() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("Json files (*.json", "*.json");
        fileChooser.getExtensionFilters().addAll(xmlFilter, jsonFilter);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            stage.setTitle("Game Tree Builder - " + file.getName());
            Preferences.userNodeForPackage(GUI.class).put("filePath", file.getPath());

            FileChooser.ExtensionFilter selectedExtensionFilter = fileChooser.getSelectedExtensionFilter();
            if (xmlFilter.equals(selectedExtensionFilter)) {
                modelIO.saveAsXml(file);
            } else if (jsonFilter.equals(selectedExtensionFilter)) {
                modelIO.saveAsJson(file);
            }

            modelIO.setMostRecentSavedFile(file);
        }
    }

    @FXML
    public void saveWorkspace() {
        if (modelIO.getMostRecentSavedFile() == null) {
            saveAsWorkspace();
        } else {
            modelIO.saveAsXml(modelIO.getMostRecentSavedFile());
        }
    }

    @FXML
    public void openSave() throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("Are you sure you want to load this file? All unsaved progress will be lost.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                GUI newWindow = newWindow();
                newWindow.loadFile(file);
                closeApplication();
            }
        }
    }

    @FXML
    public GUI newWindow() throws Exception {
        GUI newInstance = new GUI();
        newInstance.init();
        newInstance.start(new Stage());
        return newInstance;
    }

    void init(Stage stage, ModelIO modelIO) {
        this.stage = stage;
        this.modelIO = modelIO;
    }

    @FXML
    public void closeApplication() {
        stage.close();
    }
}
