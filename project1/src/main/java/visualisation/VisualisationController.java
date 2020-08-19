package main.java.visualisation;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import main.java.scheduler.Scheduler;

import java.util.Timer;
import java.util.TimerTask;

public class VisualisationController {
    private Scheduler sc;
    @FXML
    private JFXButton xd;
    @FXML
    private TextField text;
    @FXML
    private VBox bestScheduleParent;
    @FXML
    private VBox currentScheduleParent;
    @FXML
    private ScheduleChart<Number, String> currentScheduleChart;
    @FXML
    private ScheduleChart<Number, String> bestScheduleChart;

    @FXML
    void initialize() {
        sc = VisualisationDriver.sc;
        // Setup polling the scheduler
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshStats();
            }

        }, 1000, 1000);

        setup();
    }

    private void setup() {
        bestScheduleParent.getChildren().add(bestScheduleChart);
        currentScheduleParent.getChildren().add(currentScheduleChart);
    }

    private void refreshStats() {
        System.out.println("Updating statistics");
    }

    @FXML
    void exampleHandler(ActionEvent event) {
        System.out.println("You called me?");
        text.appendText("xd");
    }
}
