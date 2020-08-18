package main.java.visualisation;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.java.scheduler.Scheduler;

import java.util.Timer;
import java.util.TimerTask;

public class VisualisationController {

    private int REFRESH_RATE = 1000;

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
    private AreaChart<?, ?> CPUGraph;

    @FXML
    private VBox RAMParent;

    @FXML
    private AreaChart<?, ?> RAMGraph;

    @FXML
    private Text timeElapsedFigure;

    @FXML
    private Text visitedStatesFigure;

    @FXML
    private Text completedSchedulesFigure;

    @FXML
    void initialize() {
        seconds = 0;
        minutes = 0;

        // start the overall timer
        startTimer();
        sc = VisualisationDriver.sc;
        // Setup polling the scheduler
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                updateStatistics();
            }
        }, REFRESH_RATE, 1000);
    }

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

    private void updateStatistics() {

        long visitedStates = sc.getTotalStatesVisited();
        long completedSchedules = sc.getCompleteStatesVisited();

        visitedStatesFigure.setText(Long.toString(visitedStates));
        completedSchedulesFigure.setText(Long.toString(completedSchedules));
    }
}
