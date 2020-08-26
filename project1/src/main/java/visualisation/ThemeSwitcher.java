package main.java.visualisation;

import javafx.scene.Scene;
import main.java.visualisation.ganttchart.ScheduleChart;

public class ThemeSwitcher {
    private String _theme = "light";
    private Scene _scene;

    public ThemeSwitcher(Scene scene, String initialCss) {
        _scene = scene;
        setCss(initialCss);
    }

    public void switchTheme() {
        if (_theme.equals("light")) {
            _theme = "dark";
            setCss("dark-style.css");
        } else {
            _theme = "light";
            setCss("light-style.css");
        }
    }

    private void setCss(String cssFile) {
        _scene.getStylesheets().clear();
        _scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
    }

}
