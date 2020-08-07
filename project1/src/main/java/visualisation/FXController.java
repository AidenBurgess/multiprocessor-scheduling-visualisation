package main.java.visualisation;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.java.scheduler.Scheduler;

import java.util.TimerTask;

public class FXController extends Application {
    public static Scheduler sc = null;

    public static void main(Scheduler scheduler) {
        FXController.sc = scheduler;

        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        // Setup polling the scheduler
        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 1000, 1000);


        primaryStage.setTitle("Hello World!");

        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction((ActionEvent event) -> {
                System.out.println("Our scheduler has " + FXController.sc.numProcessors + " processors!");
                System.out.println("Hello World!");
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public void onRefresh() {
        System.out.println("This will run every 1 seconds");
    }
}