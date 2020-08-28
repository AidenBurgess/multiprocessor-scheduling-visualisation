package main.java.visualisation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Allows a window to be draggable anywhere.
 */
public class DraggableWindow {
    @FXML
    AnchorPane root;
    // The stage to make draggable
    Stage stage;
    // Variables record how far our current mouse is from the initial.
    double xOffset = 20;
    double yOffset = 20;

    /**
     * Adds listeners to the root of the stage, allowing it to be dragged.
     * Should be called after initialisation.
     */
    public void makeStageDraggable() {
        // Add these handlers after application is initialised.
        Platform.runLater(() -> {
            stage = (Stage) root.getScene().getWindow();
            root.setOnMousePressed(event-> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            root.setOnMouseDragged(event-> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
                // Make stage opaque when being dragged.
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
