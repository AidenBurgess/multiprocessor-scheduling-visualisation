package main.java.visualisation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.chart.XYChart.Series;
import main.java.dotio.TaskGraph;
import main.java.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//todo Ask the group if we want the GanttChart class named to be
// changed or we want to retain it. Also whether we want the fields
// in the GanttChart class should start with underscore

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
    }
}
