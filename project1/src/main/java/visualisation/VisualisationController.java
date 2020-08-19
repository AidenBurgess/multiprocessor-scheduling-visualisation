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
import main.java.dotio.Task;
import main.java.dotio.TaskGraph;
import main.java.scheduler.Scheduler;
import main.java.visualisation.ScheduleChart.ExtraData;

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
    private VBox _bestScheduleParent;
    @FXML
    private VBox _currentScheduleParent;

    // Non-FXML Fields
    private ScheduleChart<Number, String> _currentScheduleChart;
    private ScheduleChart<Number, String> _bestScheduleChart;
    private Scheduler _scheduler;
    private List<Task> _taskList;
    private int _numProcessors;

    @FXML
    void initialize() {
        _scheduler = VisualisationDriver.getScheduler();
        _taskList = VisualisationDriver.getTaskGraph().getTasks();
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
        // Setting up the x-axis
        List<String> processorsList = new ArrayList<String>();
        for (int i=0; i < _numProcessors; i++) {
            processorsList.add("Processor "+i+1);
        }
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setCategories(FXCollections.observableArrayList(processorsList));
        yAxis.setLabel("Processors");

        // Setting up the y-axis
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");

        // Setting up the Schedule chart object and their parents (containers)
        _currentScheduleChart = new ScheduleChart<Number, String>(xAxis, yAxis);
        _bestScheduleChart = new ScheduleChart<Number, String>(xAxis, yAxis);
        _bestScheduleParent.getChildren().add(_bestScheduleChart);
        _currentScheduleParent.getChildren().add(_currentScheduleChart);
    }

    //todo make sure that the following case related to this method is handled: When the scheduler has not found a
    // best schedule yet and this method is called. Either prevent this from happening or handle this situation inside the method
    private void updateScheduleChart() {
        // Create Series objects. Each object will act as a row in the chart
        Series[] seriesArray = new Series[_numProcessors];
        for (int i = 0; i < _numProcessors; i++) {
            seriesArray[i] = new Series();
        }

        // Run through each task, create an XYChart.Data object and put
        // this object in the Series object which corresponds to the processor this task is scheduled on
        for (Task task : _taskList) {
            int processorOfTask = _scheduler.getProcessorMap().get(task.getName());
            int taskStartTime = _scheduler.getStartTimeMap().get(task.getName());
            int taskTime = task.getTaskTime();

            XYChart.Data taskData = new XYChart.Data(taskStartTime, "Processor "+processorOfTask, new ExtraData(taskTime));

            // -1 has been used below because the seriesArray is 0 indexed whereas the processor numbers are 1 indexed
            seriesArray[processorOfTask-1].getData().add(taskData);
        }

        // Put the Series objects generated above in the chart after clearing the chart's existing data
        _bestScheduleChart.getData().clear();
        for (Series series : seriesArray) {
            _bestScheduleChart.getData().add(series);
        }
    }

    private void refreshStats() {
        System.out.println("Updating statistics");
    }

    @FXML
    void exampleHandler(ActionEvent event) {
        System.out.println("You called me?");
    }
}
