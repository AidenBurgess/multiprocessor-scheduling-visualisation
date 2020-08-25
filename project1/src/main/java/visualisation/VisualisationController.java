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
    private VBox currentSchedule;

    @FXML
    private VBox bestSchedule;

    @FXML
    private VBox statistics;

    @FXML
    private VBox CPUParent;

    @FXML
    private AreaChart<String, Number> CPUChart;

    @FXML
    private VBox RAMParent;

    @FXML
    private AreaChart<Number, Number> RAMChart;

    @FXML
    private Text timeElapsedFigure;

    @FXML
    private Text activeBranchFigure;

    @FXML
    private Text visitedStatesFigure;

    @FXML
    private Text completedSchedulesFigure;

    @FXML
    private VBox _bestScheduleParent;

    @FXML
    private VBox _currentScheduleParent;

    // Non-FXML Fields
    private ScheduleChart<Number, String> _currentScheduleChart;
    private ScheduleChart<Number, String> _bestScheduleChart;

    private int _numProcessors;





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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        _informationHolder = VisualisationDriver.getInformationHolder();
        _performanceRetriever = new SystemPerformanceRetriever();
        _taskList = VisualisationDriver.getTaskGraph().getTasks();
        _numProcessors = VisualisationDriver.getNumProcessors();

        // initialise the time to 0.00
        _seconds = 0;
        _milliseconds = 0;

        // start the overall timer
        startTimer();

        // initialise the charts
        setUpCPUChart();
        setUpRAMChart();
        setUpScheduleCharts();

        // Setup polling the scheduler
        _timer = new Timer();

        _timer.schedule(new TimerTask() {

            @Override
            public void run() {
                updateStatistics();

                // queue tasks on the other thread
                Platform.runLater(new Runnable() {
                    int i = 0;
                    @Override
                    public void run() {
//                        CPUSeries.getData().add(new XYChart.Data<>(Integer.toString(i++),10));
                        addCPUChartData();
                        addRAMChartData();
                        updateScheduleChart();
//                        if (i++ == 5) {
//
//                            i = 0;
//                        }
                    }
                });
            }

        }, _refreshRate, 1000);
    }

    private void setUpScheduleCharts() {
        // Setting up the y-axis
        List<String> processorsList = new ArrayList<String>();
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
        _currentScheduleChart = new ScheduleChart<Number, String>(xAxis, yAxis);
        _bestScheduleChart = new ScheduleChart<Number, String>(xAxis, yAxis);

        _bestScheduleChart.setBlockHeight(200/_numProcessors);
        _currentScheduleChart.setBlockHeight(200/_numProcessors);


        _bestScheduleParent.getChildren().add(_bestScheduleChart);
        _currentScheduleParent.getChildren().add(_currentScheduleChart);

        // Setting up the stylesheet for the charts
        _bestScheduleChart.getStylesheets().add(getClass().getResource("scheduleChart.css").toExternalForm());
    }


    public void shutdown() {
        System.exit(0);
    }
}
