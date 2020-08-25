package main.java.visualisation;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.text.Text;
import main.java.visualisation.ganttchart.ScheduleChart;

import java.util.*;
import java.net.URL;

//todo Ask the group if we want the GanttChart class named to be
// changed or we want to retain it. Also whether we want the fields
// in the GanttChart class should start with underscore

public class VisualisationController implements Initializable {


    private XYChart.Series _CPUSeries;
    private XYChart.Series _RAMSeries;



    // FXML Fields

    @FXML
    private AreaChart<String, Number> CPUChart;

    @FXML
    private AreaChart<Number, Number> RAMChart;

    @FXML
    private Text _timeElapsedFigure;

    @FXML
    private Text _activeBranchFigure;

    @FXML
    private Text _visitedStatesFigure;

    @FXML
    private Text _completedSchedulesFigure;

    @FXML
    private VBox _bestScheduleParent;

    @FXML
    private VBox _currentScheduleParent;

    // Non-FXML Fields
    private ScheduleChart<Number, String> _currentScheduleChart;
    private ScheduleChart<Number, String> _bestScheduleChart;

    private int _numProcessors;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        _numProcessors = VisualisationDriver.getNumProcessors();
        // initialise the charts
        setUpCPUChart();
        setUpRAMChart();
        setUpScheduleCharts();

        DisplayUpdater displayUpdater = new DisplayUpdater(_visitedStatesFigure, _completedSchedulesFigure, _activeBranchFigure, _timeElapsedFigure,
                                                           _currentScheduleChart, _bestScheduleChart, _CPUSeries, _RAMSeries);

        InformationPoller informationPoller = new InformationPoller(displayUpdater);
    }


    /**
     * Sets up the RAM chart
     */
    private void setUpRAMChart() {

        // create the series data instance
        _RAMSeries = new XYChart.Series();

        // add the series data to the chart
        RAMChart.getData().add(_RAMSeries);
    }

    /**
     * Sets up the CPU chart.
     */
    private void setUpCPUChart() {

        // create the series data instance
        _CPUSeries = new XYChart.Series();

        // add the series data to the chart
        CPUChart.getData().add(_CPUSeries);
    }


    private void setUpScheduleCharts() {
        // Setting up the y-axis
        List<String> processorsList = new ArrayList<>();
        for (int i = 0; i < _numProcessors; i++) {
            processorsList.add("Processor ".concat(Integer.toString(i + 1)));
        }
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setCategories(FXCollections.observableArrayList(processorsList));
        yAxis.setLabel("Processors");

        // Setting up the x-axis
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");

        // Setting up the Schedule chart object and their parents (containers)
        _currentScheduleChart = new ScheduleChart<>(xAxis, yAxis);
        _bestScheduleChart = new ScheduleChart<>(xAxis, yAxis);

        _bestScheduleChart.setBlockHeight(200 / _numProcessors);
        _currentScheduleChart.setBlockHeight(200 / _numProcessors);


        _bestScheduleParent.getChildren().add(_bestScheduleChart);
        _currentScheduleParent.getChildren().add(_currentScheduleChart);

        // Setting up the stylesheet for the charts
        _bestScheduleChart.getStylesheets().add(getClass().getResource("ganttchart/scheduleChart.css").toExternalForm());
    }


    public void shutdown() {
        System.exit(0);
    }
}
