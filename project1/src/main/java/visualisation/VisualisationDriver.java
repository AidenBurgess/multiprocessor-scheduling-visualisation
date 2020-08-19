package main.java.visualisation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.dotio.TaskGraph;
import main.java.scheduler.Scheduler;
import java.io.IOException;


/**
 *
 * @todo update methods
 */
public class VisualisationDriver extends Application {
    private static Scheduler _scheduler = null;
    private static TaskGraph _taskGraph = null;
    private static int _numProcessors = 0;

    public static void main(Scheduler scheduler, TaskGraph taskGraph, int numProcessors) {
        _scheduler = scheduler;
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("VisualisationDashboard.fxml"));
        primaryStage.setTitle("Visualisation");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    public static Scheduler getScheduler() {
        return _scheduler;
    }

    public static TaskGraph getTaskGraph() {
        return _taskGraph;
    }

    public static int getNumProcessors() {
        return _numProcessors;
    }
}
