package main.java.visualisation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import main.java.scheduler.Scheduler;

import java.util.Timer;
import java.util.TimerTask;

public class VisualisationController {

    // FXML Fields
    @FXML
    @FXML
    private VBox currentScheduleParent;

    // Non-FXML Fields
    private ScheduleChart<Number, String> _currentScheduleChart;
    private ScheduleChart<Number, String> _bestScheduleChart;
    private Scheduler _scheduler;
    private TaskGraph _taskGraph;
    private int _numProcessors;

    @FXML
    void initialize() {
        _scheduler = VisualisationDriver.getScheduler();
        _taskGraph = VisualisationDriver.getTaskGraph();
        _numProcessors = VisualisationDriver.getNumProcessors();
        // Setup polling the scheduler
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshStats();
            }
        }, 1000, 1000);

        chartSetup();
    }

    private void chartSetup() {
        bestScheduleParent.getChildren().add(bestScheduleChart);
        currentScheduleParent.getChildren().add(currentScheduleChart);
    }

    private void refreshStats() {
        System.out.println("Updating statistics");
    }

    @FXML
    void exampleHandler(ActionEvent event) {
        System.out.println("You called me?");
        text.appendText("xd");
    }
}
