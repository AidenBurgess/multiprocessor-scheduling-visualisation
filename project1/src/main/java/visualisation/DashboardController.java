package main.java.visualisation;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.dataretriever.SystemPerformanceRetriever;
import main.java.scheduler.Scheduler;

import java.util.Timer;
import java.util.TimerTask;

public class DashboardController {
    private Scheduler sc;
    private SystemPerformanceRetriever statsRetreiver = new SystemPerformanceRetriever();

    @FXML
    private JFXButton xd;
    @FXML
    private TextField text;

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
    }

    private void refreshStats() {
        System.out.println("Updating statistics");
        System.out.printf("Current CPU usage: %.2f%%%n", statsRetreiver.getCPUUsagePercent());
        System.out.printf("Current RAM usage: %dMB%n", (statsRetreiver.getRAMUsageBytes()/(1024*1024)));
        System.out.printf("Time elapsed: %dms%n", statsRetreiver.getTimeElapsed()/1000000);
    }

    @FXML
    void exampleHandler(ActionEvent event) {
        System.out.println("You called me?");
        text.appendText("xd");
    }
}
