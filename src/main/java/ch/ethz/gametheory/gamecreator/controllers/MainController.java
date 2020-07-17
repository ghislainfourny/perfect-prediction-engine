package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.ModelIO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private SidePaneController sidePaneController;
    @FXML
    private WorkspaceController workspaceController;
    @FXML
    private MenuController menuController;

    @FXML
    private Label lblZoom;
    @FXML
    private Slider sliderZoom;
    @FXML
    private TabPane mainTabPane;

    private Model model;
    private ModelIO modelIO;

    public void setStage(Stage stage) {
        stage.getScene().setOnKeyPressed(e -> {
            if (!e.isControlDown()) {
                switch (e.getCode()) {
                    case DELETE:
                        model.deleteSelectedNodes();
                        break;
                    default:
                }
            }
        });
        menuController.init(stage, modelIO);
    }

    public void loadFile(File file) {
        modelIO.loadFile(file);
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        model = new Model();
        modelIO = new ModelIO(model);

        model.scaleProperty().bind(sliderZoom.valueProperty());
        lblZoom.textProperty().bind(model.scaleProperty().multiply(100).asString("%.0f").concat("%"));

        sidePaneController.init(model);
        workspaceController.init(model);
    }
}
