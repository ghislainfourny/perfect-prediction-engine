package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MenuController {

    private MainController mainController;
    private File mostRecentSavedFile;

    @FXML public void saveAsWorkspace() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(mainController.getStage());

        if (file != null){
            if (!file.getPath().endsWith(".xml")){
                file = new File(file.getPath() + ".xml");
            }
            mainController.getStage().setTitle("Game Tree Builder - " + file.getName());
            Preferences.userNodeForPackage(GUI.class).put("filePath", file.getPath());
            this.mainController.saveState(file);
            this.mostRecentSavedFile = file;
        }
    }

    @FXML public void saveWorkspace() {
        if (mostRecentSavedFile == null)
            saveAsWorkspace();
        else
            this.mainController.saveState(mostRecentSavedFile);
    }

    @FXML public void openSave() throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(mainController.getStage());

        if (file != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("Are you sure you want to load this file? All unsaved progress will be lost.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                GUI newWindow = newWindow();
                newWindow.loadFile(file);
                closeApplication();
            }
        }
    }

    @FXML public GUI newWindow() throws Exception {
        GUI newInstance = new GUI();
        newInstance.init();
        newInstance.start(new Stage());
        return newInstance;
    }

    void init(MainController mainController){
        this.mainController = mainController;
    }

    public void setMostRecentSavedFile(File mostRecentSavedFile) {
        this.mostRecentSavedFile = mostRecentSavedFile;
    }

    @FXML public void closeApplication() {
        mainController.getStage().close();
    }
}
