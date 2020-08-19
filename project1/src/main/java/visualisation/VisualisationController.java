package main.java.visualisation;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
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

    private int REFRESH_RATE = 1000;
    private SystemPerformanceRetriever performanceRetriever;

    private XYChart.Series CPUSeries;
    private XYChart.Series RAMSeries;

    private Scheduler sc;
    private Timer t;

    private int seconds;
    private int milliseconds;

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

    private void updateRefreshRate(int refreshRate) {
        System.out.println("Updating statistics");

    }

    private void startTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0),
                e -> updateTime()),
                new KeyFrame(Duration.millis(10)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateTime() {
        if (milliseconds < 99) {
            milliseconds++;
        } else {
            milliseconds = 0;
            if (seconds < 59) {
                seconds++;
            }
        }
        timeElapsedFigure.setText(Integer.toString(seconds).concat(".").concat(Integer.toString(milliseconds)).concat("s"));
    }

    private void updateStatistics() {

        long visitedStates = sc.getTotalStatesVisited();
        long completedSchedules = sc.getCompleteStatesVisited();

        visitedStatesFigure.setText(Long.toString(visitedStates));
        completedSchedulesFigure.setText(Long.toString(completedSchedules));
    }

    private void setUpRAMChart() {

        RAMSeries = new XYChart.Series();

        RAMChart.getData().add(RAMSeries);
    }

    private void setUpCPUChart() {

        // create the X and Y axis
        CPUSeries = new XYChart.Series();
//        series.setName("CPU Usage");
//        CPUSeries.getData().add(new XYChart.Data(Integer.toString(minutes * 60 + seconds),1));
        CPUChart.getData().add(CPUSeries);

//        CPUSeries.getData().add(new XYChart.Data("howdy",10));
    }

    private void addRAMChartData() {

        // get the machine's CPU Usage data
        long RAMUsageInBytes = performanceRetriever.getRAMUsageBytes();

        // get the current time in seconds
        int time = seconds;

        RAMSeries.getData().add(new XYChart.Data(Integer.toString(time), RAMUsageInBytes));
    }

    private void addCPUChartData() {

        // get the machine's CPU Usage data
        double CPUUsage = performanceRetriever.getCPUUsagePercent();

        // get the current time in seconds
        int time = seconds;

        CPUSeries.getData().add(new XYChart.Data(Integer.toString(time), CPUUsage));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sc = VisualisationDriver.sc;
        performanceRetriever = new SystemPerformanceRetriever();

        seconds = 0;
        milliseconds = 0;

        // start the overall timer
        startTimer();
        setUpCPUChart();
        setUpRAMChart();

        // Setup polling the scheduler
        t = new Timer();

        t.schedule(new TimerTask() {

            @Override
            public void run() {
                updateStatistics();
//                addCPUChartData();

                Platform.runLater(new Runnable() {
                    @Override public void run() {
//                        CPUSeries.getData().add(new XYChart.Data<>(Integer.toString(i++),10));
                        addCPUChartData();
                        addRAMChartData();
                    }
                });
            }

        }, REFRESH_RATE, 1000);
    }
}
