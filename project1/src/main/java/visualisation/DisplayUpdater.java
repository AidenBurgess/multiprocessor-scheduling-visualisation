package main.java.visualisation;

import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import main.java.dotio.Task;
import main.java.scheduler.InformationHolder;

import java.util.HashMap;
import java.util.List;

public class DisplayUpdater {

    private ScheduleChart<Number, String> _currentScheduleChart;
    private ScheduleChart<Number, String> _bestScheduleChart;
    private XYChart.Series _CPUSeries;
    private XYChart.Series _RAMSeries;
    private Text _visitedStatesFigure;
    private Text _completedSchedulesFigure;
    private Text _timeElapsedFigure;
    private int _numProcessors = VisualisationDriver.getNumProcessors();
    private List<Task> _taskList = VisualisationDriver.getTaskGraph().getTasks();
    private int _milliseconds = 0;
    private int _seconds = 0;


    public DisplayUpdater(Text visitedStatesFigure, Text completedSchedulesFigure, Text timeElapsedFigure,
                          ScheduleChart<Number, String> currentScheduleChart, ScheduleChart<Number, String> bestScheduleChart,
                          XYChart.Series CPUSeries, XYChart.Series RAMSeries) {

        _visitedStatesFigure = visitedStatesFigure;
        _completedSchedulesFigure = completedSchedulesFigure;
        _timeElapsedFigure = timeElapsedFigure;
        _currentScheduleChart = currentScheduleChart;
        _bestScheduleChart = bestScheduleChart;
        _CPUSeries = CPUSeries;
        _RAMSeries = RAMSeries;

    }

    protected void updateTime() {
        if (_milliseconds < 99) {
            _milliseconds++;
        } else {
            _milliseconds = 0;
            _seconds++;
        }
        _timeElapsedFigure.setText(Integer.toString(_seconds).concat(".").concat(Integer.toString(_milliseconds)).concat("s"));
    }
    //todo make sure that the following case related to this method is handled: When the scheduler has not found a
    // best schedule yet and this method is called. Either prevent this from happening or handle this situation inside the method
    protected void refreshScheduleCharts(HashMap<String, Integer> currentProcessorMap, HashMap<String, Integer> bestProcessorMap,
                                         HashMap<String, Integer> currentStartTimeMap, HashMap<String, Integer> bestStartTimeMap) {

        // Create Series objects. Each object will act as a row in the respective chart
        XYChart.Series[] seriesArrayCurrent = new XYChart.Series[_numProcessors];
        XYChart.Series[] seriesArrayBest = new XYChart.Series[_numProcessors];
        for (int i = 0; i < _numProcessors; i++) {
            seriesArrayCurrent[i] = new XYChart.Series();
            seriesArrayBest[i] = new XYChart.Series();
        }

        // Run through each task, create an XYChart.Data object and put
        // this object in the Series object which corresponds to the processor this task is scheduled on
        for (Task task : _taskList) {
            int taskTime = task.getTaskTime();

            // Populating the current schedule if the schedule contains the current task
            /**
             * todo When current state is provided by the InformationHolder, both task-blocks show up on the
             * same side. I commented this out for now.
             */
//            if (currentStartTimeMap.containsKey(task.getName())) {
//                int taskProcessorCurrent = currentProcessorMap.get(task.getName());
//                int taskStartTimeCurrent = currentStartTimeMap.get(task.getName());
//                XYChart.Data taskDataCurrent = new XYChart.Data(taskStartTimeCurrent, "Processor ".concat(Integer.toString(taskProcessorCurrent)), new ExtraData(taskTime, "task"));
//                // -1 has been used below because the seriesArray is 0 indexed whereas the processor numbers are 1 indexed
//                seriesArrayBest[taskProcessorCurrent - 1].getData().add(taskDataCurrent);
//            }

            // Populating the best schedule chart
            int taskProcessorBest = bestProcessorMap.get(task.getName());
            int taskStartTimeBest = bestStartTimeMap.get(task.getName());
            XYChart.Data taskDataBest = new XYChart.Data(taskStartTimeBest, "Processor ".concat(Integer.toString(taskProcessorBest)), new ScheduleChart.ExtraData(taskTime, "task"));
            // -1 has been used below because the seriesArray is 0 indexed whereas the processor numbers are 1 indexed
            seriesArrayBest[taskProcessorBest - 1].getData().add(taskDataBest);
        }

        // Put the Series objects in the charts after clearing the charts existing data
        _currentScheduleChart.getData().clear();
        _bestScheduleChart.getData().clear();
        for (int i = 0; i < seriesArrayCurrent.length; i++) {
            _currentScheduleChart.getData().add(seriesArrayCurrent[i]);
            _bestScheduleChart.getData().add(seriesArrayBest[i]);
        }
    }

}
