package main.java.visualisation;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.text.Text;
import main.java.dataretriever.SystemPerformanceRetriever;
import main.java.visualisation.ganttchart.ScheduleChart;

import java.util.*;
import java.net.URL;

public class VisualisationController extends DraggableWindow implements Initializable {
    // The nodes in VisualisationDashboard.fxml
    @FXML
    private AnchorPane root;
    @FXML
    private Text _bestScheduleTitle;
    @FXML
    private AreaChart<Number, Number> _CPUChart;
    @FXML
    private AreaChart<Number, Number> _RAMChart;
    @FXML
    private Text _timeElapsedFigure;
    @FXML
    private Text _activeBranchFigure;
    @FXML
    private Text _visitedStatesFigure;
    @FXML
    private Text _status;
    @FXML
    private JFXSpinner _statusSpinner;
    @FXML
    private Text _completedSchedulesFigure;
    @FXML
    private VBox _bestScheduleParent;
    @FXML
    private VBox _currentScheduleParent;
    @FXML
    private HBox _upperHBox;
    @FXML
    private ImageView _switchThemeIcon;

    // The data for the charts and chart objects
    private XYChart.Series _CPUSeries;
    private XYChart.Series _RAMSeries;
    private int _numProcessors;
    private static final int TASK_HEIGHT_DETERMINANT = 200;
    private ScheduleChart<Number, String> _currentScheduleChart;
    private ScheduleChart<Number, String> _bestScheduleChart;

    // Related to the retrieval of CPU and RAM information
    private SystemPerformanceRetriever _performanceRetriever;

    // Styling
    private ThemeSwitcher _themeSwitcher;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the light theme when the scene is loaded
        Platform.runLater(() -> {
            Scene scene = root.getScene();
            _themeSwitcher = new ThemeSwitcher(scene, _switchThemeIcon, "css/light-style.css");
        });

        // Initialise the charts after initialising the fields required by them
        _performanceRetriever = new SystemPerformanceRetriever();
        _numProcessors = VisualisationDriver.getNumProcessors();
        setUpCPUChart();
        setUpRAMChart();
        setUpScheduleCharts();

        // The information poller once created starts polling the scheduler for information to display
        // on the visualisation module. The display updater methods are called by the information poller.
        DisplayUpdater displayUpdater = new DisplayUpdater(_visitedStatesFigure, _completedSchedulesFigure,
                _activeBranchFigure, _timeElapsedFigure, _status, _statusSpinner, _currentScheduleChart, _bestScheduleChart, _bestScheduleTitle, _CPUSeries,
                _RAMSeries, _upperHBox);
        InformationPoller informationPoller = new InformationPoller(displayUpdater, _performanceRetriever);
    }

    /**
     * Sets up the RAM chart
     */
    private void setUpRAMChart() {

        // Contains the data which will be fed to the RAM chart.
        _RAMSeries = new XYChart.Series();

        // Remove animations from axes
        Platform.runLater(() -> {
            XYChart chart = _RAMSeries.getChart();
            chart.getXAxis().setAnimated(false);
            chart.getYAxis().setAnimated(false);
        });
        
        // Add the series data to the chart
        _RAMChart.getData().add(_RAMSeries);
        NumberAxis yAxis = (NumberAxis) _RAMChart.getYAxis();
        // Set the axis upper bound and the vertical step size
        yAxis.setUpperBound(Math.ceil(_performanceRetriever.getTotalRAMGigaBytes()));
        yAxis.setTickUnit(1);
    }

    /**
     * Sets up the CPU chart.
     */
    private void setUpCPUChart() {

        // create the series data instance
        _CPUSeries = new XYChart.Series();

        // Remove animations from axes
        Platform.runLater(() -> {
            XYChart chart = _CPUSeries.getChart();
            chart.getXAxis().setAnimated(false);
            chart.getYAxis().setAnimated(false);
        });

        // add the series data to the chart and set maximum bound and tick unit
        _CPUChart.getData().add(_CPUSeries);
        NumberAxis yAxis = (NumberAxis) _CPUChart.getYAxis();
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);

    }

    /**
     * Set up both schedule charts and add the charts to their parent components.
     */
    private void setUpScheduleCharts() {

        _currentScheduleChart = setUpScheduleChart();
        _currentScheduleParent.getChildren().add(_currentScheduleChart);

        _bestScheduleChart = setUpScheduleChart();
        _bestScheduleParent.getChildren().add(_bestScheduleChart);
    }

    /**
     * Initialise a schedule chart with x-axis and y-axis values and titles.
     * 
     * @return The generated ScheduleChart object.
     */
    private ScheduleChart<Number, String> setUpScheduleChart() {

        // Setting up the y-axis
        List<String> processorsList = new ArrayList<>();
        for (int i = 0; i < _numProcessors; i++) {
            processorsList.add("Processor ".concat(Integer.toString(i + 1)));
        }
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setAnimated(false);
        yAxis.setCategories(FXCollections.observableArrayList(processorsList));

        // Setting up the x-axis
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");
        xAxis.setAnimated(false);


        // Setting up the Schedule chart object and their parents (containers)
        ScheduleChart<Number, String> scheduleChart = new ScheduleChart<>(xAxis, yAxis);
        scheduleChart.setBlockHeight(TASK_HEIGHT_DETERMINANT/_numProcessors);
        return scheduleChart;
    }

    @FXML
    public void switchTheme() {
        _themeSwitcher.switchTheme();
    }

    @FXML
    public void minimise() {
        stage.setIconified(true);
    }

    @FXML
    public void close() {
        shutdown();
    }

    public void shutdown() {
        System.exit(0);
    }
}
