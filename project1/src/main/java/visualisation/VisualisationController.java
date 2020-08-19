package main.java.visualisation;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

    private Scheduler sc;
    private Timer t;

    private int seconds;
    private int minutes;

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

//    @FXML
//    void initialize() {
//        seconds = 0;
//        minutes = 0;
//
//        // start the overall timer
//        startTimer();
//        setUpCPUChart();
//
//        sc = VisualisationDriver.sc;
//        // Setup polling the scheduler
//        t = new Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                updateStatistics();
//            }
//        }, REFRESH_RATE, 1000);
//    }

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
        if (seconds < 59) {
            seconds++;
        } else {
            seconds = 0;
            if (minutes < 59) {
                minutes++;
            }
        }
        timeElapsedFigure.setText(Integer.toString(minutes).concat(".").concat(Integer.toString(seconds)).concat("s"));
    }

    private void updateStatistics(int i) {

        long visitedStates = sc.getTotalStatesVisited();
        long completedSchedules = sc.getCompleteStatesVisited();

        visitedStatesFigure.setText(Long.toString(visitedStates) + i);
        completedSchedulesFigure.setText(Long.toString(completedSchedules) + i);
    }

    private void setUpCPUChart() {

        // create the X and Y axis
        XYChart.Series series = new XYChart.Series<>();
//        series.setName("CPU Usage");
        series.getData().add(new XYChart.Data("Monday",1));
        series.getData().add(new XYChart.Data("Tuesday",5));
        series.getData().add(new XYChart.Data("Wednesday",100));

        CPUChart.getData().add(series);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sc = VisualisationDriver.sc;
        performanceRetriever = new SystemPerformanceRetriever();

        seconds = 0;
        minutes = 0;

        // start the overall timer
        startTimer();
        setUpCPUChart();

        // Setup polling the scheduler
        t = new Timer();

        t.schedule(new TimerTask() {
            int i = 1;
            @Override
            public void run() {
                updateStatistics(i);
                i += 1005;
            }
        }, REFRESH_RATE, 1000);
    }
}
