package main.java.visualisation;

import com.jfoenix.controls.JFXSpinner;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
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
    private Text _bestScheduleTitle;
    private XYChart.Series _CPUSeries;
    private XYChart.Series _RAMSeries;
    private Text _visitedStatesFigure;
    private Text _completedSchedulesFigure;
    private Text _activeBranchFigure;
    private Text _timeElapsedFigure;
    private Text _status;
    private JFXSpinner _statusSpinner;

    //Information on tasks and processors from the driver
    private int _numProcessors = VisualisationDriver.getNumProcessors();
    private List<Task> _taskList = VisualisationDriver.getTaskGraph().getTasks();

    //Fields used for timing purposes
    Timeline _timeline;
    private float _timeElapsed = 0;
    private boolean _schedulerDone = false;

    // Fields for storing the previous best schedule information
    private HashMap<String, Integer> _previousBestProcessorMap;
    private HashMap<String, Integer> _previousBestStartTimeMap;


    public DisplayUpdater(Text visitedStatesFigure, Text completedSchedulesFigure, Text activeBranchFigure, Text timeElapsedFigure,
                          Text status, JFXSpinner statusSpinner, ScheduleChart<Number, String> currentScheduleChart, ScheduleChart<Number, String> bestScheduleChart,
                          Text bestScheduleTitle, XYChart.Series CPUSeries, XYChart.Series RAMSeries) {

        _visitedStatesFigure = visitedStatesFigure;
        _completedSchedulesFigure = completedSchedulesFigure;
        _activeBranchFigure = activeBranchFigure;
        _timeElapsedFigure = timeElapsedFigure;
        _status = status;
        _statusSpinner = statusSpinner;
        _currentScheduleChart = currentScheduleChart;
        _bestScheduleChart = bestScheduleChart;
        _bestScheduleTitle = bestScheduleTitle;
        _CPUSeries = CPUSeries;
        _RAMSeries = RAMSeries;
        _previousBestProcessorMap = new HashMap<String, Integer>();
        _previousBestStartTimeMap = new HashMap<String, Integer>();
        startTimer();
    }

    /**
     * Creates a new timeline, immediately calls the updateTime() method and then calls
     * it after every 10ms
     */
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

    /**
     * Updates the _timeElapsed field every centisecond and if the scheduler is not finished yet
     * then update the time elapsed in the stats section of the visualization window
     */
    protected void updateTime() {
        _timeElapsed += 0.01;

        if (!_schedulerDone)
            _timeElapsedFigure.setText(String.format("%.2f", _timeElapsed));
    }

    /**
     * Updates the statistics such as the visited states and the completed schedules
     */
    protected void updateStatistics(long visitedStates, long completedSchedules, long activeBranches) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        _visitedStatesFigure.setText(formatter.format(visitedStates));
        _completedSchedulesFigure.setText(Long.toString(completedSchedules));
        _activeBranchFigure.setText(Long.toString(activeBranches));
        // Hide status spinner when finished
        if (_schedulerDone) _statusSpinner.setVisible(false);
        _status.setText(_schedulerDone ? "Finished! ✅" : "");
    }

    /**
     * Adds ram data to the series and updates the chart.
     */
    protected void refreshRAMChart(double RAMUsageInBytes) {
        // get the machine's CPU Usage data
        _RAMSeries.getData().add(new XYChart.Data(_timeElapsed, RAMUsageInBytes));
    }


    /**
     * Adds CPU data to the series and updates the chart.
     */
    protected void refreshCPUChart(double CPUUsage) {
        // get the machine's CPU Usage data
        _CPUSeries.getData().add(new XYChart.Data(_timeElapsed, CPUUsage));
    }
    
    protected void refreshScheduleCharts(HashMap<String, Integer> currentProcessorMap, HashMap<String, Integer> bestProcessorMap,
                                         HashMap<String, Integer> currentStartTimeMap, HashMap<String, Integer> bestStartTimeMap, long currentBound) {

        if (!bestProcessorMap.equals(_previousBestProcessorMap) || !bestStartTimeMap.equals(_previousBestStartTimeMap)) {
            refreshScheduleChart(_bestScheduleChart, bestProcessorMap, bestStartTimeMap, "best-task");
            _previousBestProcessorMap = bestProcessorMap;
            _previousBestStartTimeMap = bestStartTimeMap;
        }
        if (_schedulerDone) {
            _currentScheduleChart.getData().clear();
            _bestScheduleTitle.setText(String.format("Best Schedule: End Time = %d", currentBound));
        } else {
            refreshScheduleChart(_currentScheduleChart, currentProcessorMap, currentStartTimeMap, "current-task");
        }
    }

    private void refreshScheduleChart(ScheduleChart<Number, String> scheduleChart, HashMap<String, Integer> processorMap, HashMap<String, Integer> startTimeMap, String styleClass) {
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
                XYChart.Data taskData = new XYChart.Data(taskStartTime, "Processor ".concat(Integer.toString(taskProcessor)), new ScheduleChart.ExtraData(task.getName(), taskTime, styleClass));
                // -1 has been used below because the seriesArray is 0 indexed whereas the processor numbers are 1 indexed
                seriesArray[taskProcessor - 1].getData().add(taskData);
            }
        }

        scheduleChart.getData().clear();
        for (int i = 0; i < seriesArray.length; i++) {
            scheduleChart.getData().add(seriesArray[i]);
        }

        for (int i = 0; i < seriesArray.length; i++) {
            ObservableList<XYChart.Data> dataList = seriesArray[i].getData();
            for (XYChart.Data taskData : dataList) {
                ScheduleChart.ExtraData taskExtraData = ((ScheduleChart.ExtraData)taskData.getExtraValue());
                Tooltip t = new Tooltip("Name: ".concat(taskExtraData.getTaskName().concat("\n"+"Length: ".concat(Long.toString(taskExtraData.getLength())))));
                Tooltip.install(taskData.getNode(), t);
            }
        }
    }
}
