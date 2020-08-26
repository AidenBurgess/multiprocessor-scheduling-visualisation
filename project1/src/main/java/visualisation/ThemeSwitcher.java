package main.java.visualisation;

import javafx.scene.Scene;
import main.java.visualisation.ganttchart.ScheduleChart;

public class ThemeSwitcher {
    private String _theme = "light";
    private Scene _scene;

    public ThemeSwitcher(Scene scene, ScheduleChart<Number, String> currentScheduleChart, ScheduleChart<Number, String> bestScheduleChart, String initialCss) {
        _scene = scene;
        _currentScheduleChart = currentScheduleChart;
        _bestScheduleChart = bestScheduleChart;
        setCss(initialCss);
    }

    private ScheduleChart<Number, String> _currentScheduleChart;
    private ScheduleChart<Number, String> _bestScheduleChart;


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
        _bestScheduleChart.getStylesheets().clear();
        _currentScheduleChart.getStylesheets().clear();
        _scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
        _bestScheduleChart.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
        _currentScheduleChart.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
    }

}
