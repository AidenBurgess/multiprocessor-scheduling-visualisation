package main.java.visualisation;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.scene.Scene;
import main.java.visualisation.ganttchart.ScheduleChart;

public class ThemeSwitcher {
    private String _theme = "light";
    private Scene _scene;
    private JFXToggleButton _switchThemeButton;

    public ThemeSwitcher(Scene scene, JFXToggleButton switchThemeButton, String initialCss) {
        _scene = scene;
        _switchThemeButton = switchThemeButton;
        _switchThemeButton.setText("Dark Mode");
        setCss(initialCss);
    }

    public void switchTheme() {
        if (_theme.equals("light")) {
            _theme = "dark";
            setCss("css/dark-style.css");
            _switchThemeButton.setText("Light Mode");
        } else {
            _theme = "light";
            setCss("css/light-style.css");
            _switchThemeButton.setText("Dark Mode");
        }
    }

    private void setCss(String cssFile) {
        Platform.runLater(() -> {
            _scene.getStylesheets().clear();
            _scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
        });
    }

}
