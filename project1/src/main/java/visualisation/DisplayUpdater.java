package main.java.visualisation;

import com.jfoenix.controls.JFXSpinner;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.java.dotio.Task;
import main.java.visualisation.ganttchart.ScheduleChart;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * A class which is responsible for refreshing/updating the visualisation elements. These include the
 * current and best schedules, the CPU and RAM graphs and the statistics. The methods of this class
 * are invoked from the InformationPoller object, which periodically refreshes the visualisation elements
 * via the DisplayUpdater object. 
 */
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
    private HBox _upperHBox;

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


    /**
     * Constructor used to instantiate the DisplayUpdater object in the VisualisationController.
     * A reference to the JavaFX elements which need to be updated/refreshed by the DisplayUpdater
     * are passed into the constructor when being called in the VisualisationController.
     *
     * @param visitedStatesFigure
     * @param completedSchedulesFigure
     * @param activeBranchFigure
     * @param timeElapsedFigure
     * @param status
     * @param statusSpinner
     * @param currentScheduleChart
     * @param bestScheduleChart
     * @param bestScheduleTitle
     * @param CPUSeries
     * @param RAMSeries
     * @param upperHBox
     */
    public DisplayUpdater(Text visitedStatesFigure, Text completedSchedulesFigure, Text activeBranchFigure, Text timeElapsedFigure,
                          Text status, JFXSpinner statusSpinner, ScheduleChart<Number, String> currentScheduleChart, ScheduleChart<Number, String> bestScheduleChart,
                          Text bestScheduleTitle, XYChart.Series CPUSeries, XYChart.Series RAMSeries, HBox upperHBox) {

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
        _previousBestProcessorMap = new HashMap<>();
        _previousBestStartTimeMap = new HashMap<>();
        _upperHBox = upperHBox;
        // Change default tooltip behaviour, parameters are specified in ms.
        // start the DisplayUpdater timer automatically when the object is created
        startTimer();
    }

    /**
     * Creates a new timeline, immediately calls the updateTime() method and then calls
     * it after every 10ms
     */
    private void startTimer() {
        // create a timeline to keep track of the elapsed time being displayed and to plot the CPU and RAM graphs
        _timeline = new Timeline(new KeyFrame(Duration.millis(0),
                e -> updateTime()),
                new KeyFrame(Duration.millis(10))); // call the updateTime() method every 10ms
        _timeline.setCycleCount(Animation.INDEFINITE);
        // start the timeline
        _timeline.play();
    }

    /**
     * Method used to record the time when the algorithm is complete
     */
    protected void stopTimer() {
        _schedulerDone = true;
    }

    /**
     * Updates the _timeElapsed field every centisecond and if the scheduler is not finished yet,
     * then update the time elapsed in the stats section of the visualisation window.
     */
    protected void updateTime() {
        _timeElapsed += 0.01;

        // if the algorithm isn't complete, keep increasing the elapsed time
        if (!_schedulerDone)
            _timeElapsedFigure.setText(String.format("%.2f", _timeElapsed));
    }

    /**
     * Updates the statistics such as the visited states and the completed schedules
     *
     * @param visitedStates
     * @param completedSchedules
     * @param activeBranches
     */
    protected void updateStatistics(long visitedStates, long completedSchedules, long activeBranches) {
        // create a decimal formatter to add commas to the number of visited states
        DecimalFormat formatter = new DecimalFormat("#,###");
        // display the stats
        _visitedStatesFigure.setText(formatter.format(visitedStates));
        _completedSchedulesFigure.setText(Long.toString(completedSchedules));
        _activeBranchFigure.setText(formatter.format(activeBranches));
        // Hide status spinner when finished
        if (_schedulerDone) _statusSpinner.setVisible(false);
        _status.setText(_schedulerDone ? "Finished!" : "");
    }

    /**
     * Adds ram data to the series and updates the chart.
     *
     * @param RAMUsageInBytes
     */
    protected void refreshRAMChart(double RAMUsageInBytes) {
        // get the machine's CPU Usage data and create a data object for it
        _RAMSeries.getData().add(new XYChart.Data(_timeElapsed, RAMUsageInBytes));
    }


    /**
     * Adds CPU data to the series and updates the chart.
     *
     * @param CPUUsage
     */
    protected void refreshCPUChart(double CPUUsage) {
        // get the machine's CPU Usage data and create a data object for it
        _CPUSeries.getData().add(new XYChart.Data(_timeElapsed, CPUUsage));
    }

    /**
     * Update the current and best schedule charts with the newly available data
     *
     * @param currentProcessorMap
     * @param bestProcessorMap
     * @param currentStartTimeMap
     * @param bestStartTimeMap
     * @param currentBound
     */
    protected void refreshScheduleCharts(HashMap<String, Integer> currentProcessorMap, HashMap<String, Integer> bestProcessorMap,
                                         HashMap<String, Integer> currentStartTimeMap, HashMap<String, Integer> bestStartTimeMap,
                                         long currentBound) {

        // if the best schedule has not changed since the last time it was updated, retain its state
        if (!bestProcessorMap.equals(_previousBestProcessorMap) || !bestStartTimeMap.equals(_previousBestStartTimeMap)) {
            refreshScheduleChart(_bestScheduleChart, bestProcessorMap, bestStartTimeMap, "best-task");
            _previousBestProcessorMap = bestProcessorMap;
            _previousBestStartTimeMap = bestStartTimeMap;
        }

        // Once optimal schedule is found, remove current schedule and update best schedule title to show end time
        if (_schedulerDone) {
            if (_upperHBox.getChildren().size() > 2) { // if current schedule is being displayed
                _upperHBox.getChildren().remove(0); // remove the first spacer vBox
                _upperHBox.getChildren().remove(0); // remove the current schedule node
                _upperHBox.getChildren().remove(0); // remove the middle spacer vBox
            }
            _bestScheduleTitle.setText(String.format("Optimal Schedule. Length: %d", currentBound));
            // if the optimal schedule is not found
        } else {
            // refresh the current schedule being tested by the algorithm
            refreshScheduleChart(_currentScheduleChart, currentProcessorMap, currentStartTimeMap, "current-task");
        }
    }

    /**
     * Common method used to update a ScheduleChart object. The method is used to update both, current and best
     * schedule objects, depending on the parameters provided to it. This avoids code duplication which would
     * occur from having separate methods to update each schedule chart.
     *
     * @param scheduleChart
     * @param processorMap
     * @param startTimeMap
     * @param styleClass
     */
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
                // create a new task data object to be displayed
                XYChart.Data taskData = new XYChart.Data(taskStartTime, "Processor ".concat(Integer.toString(taskProcessor)), new ScheduleChart.ExtraData(task.getName(), taskTime, styleClass));
                // -1 has been used below because the seriesArray is 0 indexed whereas the processor numbers are 1 indexed
                seriesArray[taskProcessor - 1].getData().add(taskData);
            }
        }

        // clear the schedule chart and add the new series in
        scheduleChart.getData().clear();
        for (XYChart.Series value : seriesArray) {
            scheduleChart.getData().add(value);
        }

        // For each task being displayed, add a tool tip with its name and time duration (length). The tool
        // tip is displayed when the mouse is hovered over a particular task. 
        for (XYChart.Series series : seriesArray) {
            // get the list of tasks on each processor
            ObservableList<XYChart.Data> dataList = series.getData();
            // for each task, display the tool tip
            for (XYChart.Data taskData : dataList) {
                ScheduleChart.ExtraData taskExtraData = ((ScheduleChart.ExtraData) taskData.getExtraValue());
                // create the tool tip
                String toolTipText = String.format("Name: %s\nLength: %d\nStart time: %d\nEnd time: %d", taskExtraData.getTaskName(), taskExtraData.getLength(), taskData.getXValue(), (int)taskData.getXValue()+taskExtraData.getLength());
                Tooltip t = new Tooltip(toolTipText);
                // add the tool tip to the visualisation window
                t.setShowDelay(Duration.seconds(0.1));
                Tooltip.install(taskData.getNode(), t);
            }
        }
    }
}
