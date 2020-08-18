package main.java.visualisation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.scheduler.Scheduler;
import java.io.IOException;


/**
 *
 * @todo update methods
 */
public class VisualisationDriver extends Application {
    public static Scheduler sc = null;

    public static void main(Scheduler scheduler) {
        VisualisationDriver.sc = scheduler;
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("VisualisationDashboard.fxml"));
        primaryStage.setTitle("Visualisation");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }
}
