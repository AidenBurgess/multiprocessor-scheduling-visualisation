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

}
