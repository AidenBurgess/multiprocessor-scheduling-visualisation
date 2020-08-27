package main.java.visualisation;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.visualisation.ganttchart.ScheduleChart;

public class ThemeSwitcher {
    private String _theme = "light";
    private Scene _scene;
    private ImageView _switchThemeButton;

    public ThemeSwitcher(Scene scene, ImageView switchThemeButton, String initialCss) {
        _scene = scene;
        _switchThemeButton = switchThemeButton;
        setCss(initialCss);
    }

    public void switchTheme() {
        if (_theme.equals("light")) {
            _theme = "dark";
            setCss("css/dark-style.css");
            _switchThemeButton.setImage(new Image("main/java/visualisation/icons/dark-theme.png"));
        } else {
            _theme = "light";
            setCss("css/light-style.css");
            _switchThemeButton.setImage(new Image("main/java/visualisation/icons/light-theme.png"));
        }
    }

    private void setCss(String cssFile) {
        Platform.runLater(() -> {
            _scene.getStylesheets().clear();
            _scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
        });
    }

}
