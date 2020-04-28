package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class GUI extends Application {

    MainController mainController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Game Tree Builder");
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/main.fxml"));

        Parent main = null;

        try {
            main = mainLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert main != null;

        primaryStage.setScene(new Scene(main, 900, 600));
        primaryStage.setMaximized(true);
        this.mainController = mainLoader.getController();
        this.mainController.setStage(primaryStage);
        primaryStage.show();
    }

    public void loadFile(File file){
        mainController.loadFile(file);
    }


}
