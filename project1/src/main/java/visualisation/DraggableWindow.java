package main.java.visualisation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DraggableWindow {

    @FXML
    AnchorPane root;

    Stage stage;

    double xOffset = 20;
    double yOffset = 20;

    public void makeStageDraggable() {
        Platform.runLater(() -> {
            stage = (Stage) root.getScene().getWindow();
            root.setOnMousePressed(event-> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            root.setOnMouseDragged(event-> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
                stage.setOpacity(0.8f);
            });
            root.setOnDragDone((e) -> {
                stage.setOpacity(1.0f);
                xOffset = 20;
                yOffset = 20;
            });
            root.setOnMouseReleased((e) -> {
                stage.setOpacity(1.0f);
            });
        });

    }
}