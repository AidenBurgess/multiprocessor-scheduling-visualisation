package main.java.visualisation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.dotio.TaskGraph;
import main.java.scheduler.InformationHolder;

import java.io.IOException;

/**
 * Entry point for JavaFX visualisation application.
 * Launches the {@link VisualisationController}.
 * Stores the information holder to poll scheduling details.
 * Stores taskgraph and numprocessors as configuration for the visualisation.
 */
public class VisualisationDriver extends Application {
    private static InformationHolder _informationHolder = null;
    private static TaskGraph _taskGraph = null;
    private static int _numProcessors = 0;

    /**
     * Starts the JavaFX GUI thread by calling launch().
     * Inputs are stored as static variables to be used by the visualisation.
     *
     * @param informationHolder
     * @param taskGraph
     * @param numProcessors
     */
    public static void main(InformationHolder informationHolder, TaskGraph taskGraph, int numProcessors) {
        _informationHolder = informationHolder;
        _taskGraph = taskGraph;
        _numProcessors = numProcessors;
        launch();
    }

    /**
     * Hook method called by JavaFX after GUI thread has started.
     * Specifies the window to be a fixed size, with no borders.
     *
     * @param primaryStage
     * @throws IOException if fxml file can not be found
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VisualisationDashboard.fxml"));
        Parent root = loader.load();
        VisualisationController controller = loader.getController();
        // Allow window to be dragged without border
        controller.makeStageDraggable();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        // Make window a fixed size
        stage.setResizable(false);
        // Remove borders from window
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        // Shutdown GUI thread when window is closed
        stage.setOnHidden(e -> controller.shutdown());
        stage.show();
    }

    /**
     * Retrieve the information holder object which has has information about the running scheduler.
     *
     * @return information holder object
     */
    public static InformationHolder getInformationHolder() {
        return _informationHolder;
    }

    /**
     * Retrieves the task graph which stores the input tasks and dependencies.
     *
     * @return task graph object
     */
    public static TaskGraph getTaskGraph() {
        return _taskGraph;
    }

    /**
     * Retrieves the number of processors to schedule the tasks on.
     *
     * @return number of processors
     */
    public static int getNumProcessors() {
        return _numProcessors;
    }
}
