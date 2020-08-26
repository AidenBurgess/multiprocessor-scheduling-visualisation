package main.java.visualisation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.java.dotio.Task;
import main.java.visualisation.ganttchart.ScheduleChart;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class DisplayUpdater {

    // FX Components from passed in from the controller class
    private ScheduleChart<Number, String> _currentScheduleChart;
    private ScheduleChart<Number, String> _bestScheduleChart;
    private XYChart.Series _CPUSeries;
    private XYChart.Series _RAMSeries;
    private Text _visitedStatesFigure;
    private Text _completedSchedulesFigure;
    private Text _activeBranchFigure;
    private Text _timeElapsedFigure;

    //Information on tasks and processors from the driver
    private int _numProcessors = VisualisationDriver.getNumProcessors();
    private List<Task> _taskList = VisualisationDriver.getTaskGraph().getTasks();

    //Fields used for timing purposes
    Timeline _timeline;
    private int _milliseconds = 0;
    private int _seconds = 0;
    private boolean _schedulerDone = false;


    public DisplayUpdater(Text visitedStatesFigure, Text completedSchedulesFigure, Text activeBranchFigure, Text timeElapsedFigure,
                          ScheduleChart<Number, String> currentScheduleChart, ScheduleChart<Number, String> bestScheduleChart,
                          XYChart.Series CPUSeries, XYChart.Series RAMSeries) {

        _visitedStatesFigure = visitedStatesFigure;
        _completedSchedulesFigure = completedSchedulesFigure;
        _activeBranchFigure = activeBranchFigure;
        _timeElapsedFigure = timeElapsedFigure;
        _currentScheduleChart = currentScheduleChart;
        _bestScheduleChart = bestScheduleChart;
        _CPUSeries = CPUSeries;
        _RAMSeries = RAMSeries;
        startTimer();

    }

    private void startTimer() {
        _timeline = new Timeline(new KeyFrame(Duration.millis(0),
                e -> updateTime()),
                new KeyFrame(Duration.millis(10)));
        _timeline.setCycleCount(Animation.INDEFINITE);
        _timeline.play();
    }

    protected void stopTimer() {
        _schedulerDone = true;
    }

    protected void updateTime() {
        if (_milliseconds < 99) {
            _milliseconds++;
        } else {
            _milliseconds = 0;
            _seconds++;
        }

        if (!_schedulerDone)
            _timeElapsedFigure.setText(Integer.toString(_seconds).concat(".").concat(Integer.toString(_milliseconds)).concat("s"));
    }

    /**
     * Updates the statistics such as the visited states and the completed schedules
     */
    protected void updateStatistics(long visitedStates, long completedSchedules, long activeBranches) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        _visitedStatesFigure.setText(formatter.format(visitedStates));
        _completedSchedulesFigure.setText(Long.toString(completedSchedules));
        _activeBranchFigure.setText(Long.toString(activeBranches));
    }

    /**
     * Adds ram data to the series and updates the chart.
     */
    protected void refreshRAMChart(double RAMUsageInBytes) {

        // get the machine's CPU Usage data
        _RAMSeries.getData().add(new XYChart.Data(Integer.toString(_seconds), RAMUsageInBytes));
    }


    /**
     * Adds CPU data to the series and updates the chart.
     */
    protected void refreshCPUChart(double CPUUsage) {

        // get the machine's CPU Usage data

        _CPUSeries.getData().add(new XYChart.Data(Integer.toString(_seconds), CPUUsage));
    }


    //todo make sure that the following case related to this method is handled: When the scheduler has not found a
    // best schedule yet and this method is called. Either prevent this from happening or handle this situation inside the method
    protected void refreshScheduleCharts(HashMap<String, Integer> currentProcessorMap, HashMap<String, Integer> bestProcessorMap,
                                         HashMap<String, Integer> currentStartTimeMap, HashMap<String, Integer> bestStartTimeMap) {

        refreshScheduleChart(_bestScheduleChart, bestProcessorMap, bestStartTimeMap);
        refreshScheduleChart(_currentScheduleChart, currentProcessorMap, currentStartTimeMap);

    }

    private void refreshScheduleChart(ScheduleChart<Number, String> scheduleChart, HashMap<String, Integer> processorMap, HashMap<String, Integer> startTimeMap) {

        // Create Series object. The object will act as a row in the respective chart
        XYChart.Series[] seriesArray = new XYChart.Series[_numProcessors];
        for (int i = 0; i < _numProcessors; i++) {
            seriesArray[i] = new XYChart.Series();
        }

        // Run through each task, create an XYChart.Data object and put
        // this object in the Series object which corresponds to the processor this task is scheduled on
        for (Task task : _taskList) {
            int taskTime = task.getTaskTime();
            // Populating the best schedule chart
            if (startTimeMap.containsKey(task.getName()) && processorMap.containsKey(task.getName())) {
                int taskProcessor = processorMap.get(task.getName());
                int taskStartTime = startTimeMap.get(task.getName());
                XYChart.Data taskData = new XYChart.Data(taskStartTime, "Processor ".concat(Integer.toString(taskProcessor)), new ScheduleChart.ExtraData(taskTime, "task"));
                // -1 has been used below because the seriesArray is 0 indexed whereas the processor numbers are 1 indexed
                seriesArray[taskProcessor - 1].getData().add(taskData);
            }
        }

        scheduleChart.getData().clear();
        for (int i = 0; i < seriesArray.length; i++) {
            scheduleChart.getData().add(seriesArray[i]);
        }
    }
}
