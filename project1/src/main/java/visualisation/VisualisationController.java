package main.java.visualisation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.java.dataretriever.SystemPerformanceRetriever;
import main.java.scheduler.Scheduler;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class VisualisationController implements Initializable {

    private int _refreshRate = 1000;
    private SystemPerformanceRetriever _performanceRetriever;

    private XYChart.Series _CPUSeries;
    private XYChart.Series _RAMSeries;

    private Scheduler _sc;
    private Timer _timer;

    private int _seconds;
    private int _milliseconds;

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
    private Text visitedStatesFigure;

    @FXML
    private Text completedSchedulesFigure;

    /**
     * Updates the refresh rate, yet to be implemented
     * @param refreshRate
     */
    private void updateRefreshRate(int refreshRate) {

        _refreshRate = refreshRate;
    }

    /**
     * starts the timer for the total time and updates every 10 milliseconds.
     */
    private void startTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0),
                e -> updateTime()),
                new KeyFrame(Duration.millis(10)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Updates the time from the timer.
     */
    private void updateTime() {
        if (_milliseconds < 99) {
            _milliseconds++;
        } else {
            _milliseconds = 0;
            _seconds++;
        }
        timeElapsedFigure.setText(Integer.toString(_seconds).concat(".").concat(Integer.toString(_milliseconds)).concat("s"));
    }

    /**
     * Updates the statistics such as the visited states and the completed schedules
     */
    private void updateStatistics() {

        long visitedStates = _sc.getTotalStatesVisited();
        long completedSchedules = _sc.getCompleteStatesVisited();

        visitedStatesFigure.setText(Long.toString(visitedStates));
        completedSchedulesFigure.setText(Long.toString(completedSchedules));
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

    /**
     * Adds ram data to the series and updates the chart.
     */
    private void addRAMChartData() {

        // get the machine's CPU Usage data
        double RAMUsageInBytes = _performanceRetriever.getRAMUsageBytes();

        _RAMSeries.getData().add(new XYChart.Data(Integer.toString(_seconds), RAMUsageInBytes));
    }

    /**
     * Adds CPU data to the series and updates the chart.
     */
    private void addCPUChartData() {

        // get the machine's CPU Usage data
        double CPUUsage = _performanceRetriever.getCPUUsagePercent();

        _CPUSeries.getData().add(new XYChart.Data(Integer.toString(_seconds), CPUUsage));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        _sc = VisualisationDriver.sc;
        _performanceRetriever = new SystemPerformanceRetriever();

        // initialise the time to 0.00
        _seconds = 0;
        _milliseconds = 0;

        // start the overall timer
        startTimer();

        // initialise the charts
        setUpCPUChart();
        setUpRAMChart();

        // Setup polling the scheduler
        _timer = new Timer();

        _timer.schedule(new TimerTask() {

            @Override
            public void run() {
                updateStatistics();

                // queue tasks on the other thread
                Platform.runLater(new Runnable() {
                    @Override public void run() {
//                        CPUSeries.getData().add(new XYChart.Data<>(Integer.toString(i++),10));
                        addCPUChartData();
                        addRAMChartData();
                    }
                });
            }

        }, _refreshRate, 1000);
    }
}
