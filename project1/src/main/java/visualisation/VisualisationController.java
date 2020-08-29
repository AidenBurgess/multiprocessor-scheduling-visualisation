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
import javafx.util.StringConverter;
import main.java.dataretriever.SystemPerformanceRetriever;
import main.java.visualisation.ganttchart.ScheduleChart;

import java.util.*;
import java.net.URL;

/**
 * This class is the controller for the VisualisationDashboard.fxml. It contains all the information
 * associated with the components displayed in the visualisation module. All the event handlers associated
 * with the VisualisationDashboard.fxml are contained here.
 */
public class VisualisationController extends DraggableWindow implements Initializable {
    // The nodes in VisualisationDashboard.fxml
    @FXML
    private Text _visualisationTitle; 
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

    /**
     * Called when an object of this class is created.
     * Initialises the switch theme icon, the CPU, RAM, and Schedule charts. Starts the poller which
     * polls for information to be displayed on the visualisation module.
     * @param url
     * @param resourceBundle
     */
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

        // The display updater methods are called by the information poller to update
        // the display.
        DisplayUpdater displayUpdater = new DisplayUpdater(_visitedStatesFigure, _completedSchedulesFigure,
                _activeBranchFigure, _timeElapsedFigure, _status, _statusSpinner, _currentScheduleChart, _bestScheduleChart, _bestScheduleTitle, _CPUSeries,
                _RAMSeries, _upperHBox);
        // The information poller once created starts polling for information to display
        // on the visualisation module.
        new InformationPoller(displayUpdater, _performanceRetriever);
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
        // Contains the data which will be fed to the RAM chart.
        _CPUSeries = new XYChart.Series();

        // Remove animations from axes
        Platform.runLater(() -> {
            XYChart chart = _CPUSeries.getChart();
            chart.getXAxis().setAnimated(false);
            chart.getYAxis().setAnimated(false);
        });

        // Add the series data to the chart
        _CPUChart.getData().add(_CPUSeries);
        NumberAxis yAxis = (NumberAxis) _CPUChart.getYAxis();
        // Set the axis upper bound and the vertical step size
        yAxis.setUpperBound(110);
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
        // Use only whole numbers for axis
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return Integer.toString(object.intValue());
            }
            @Override
            public Number fromString(String string) {
                return Integer.valueOf(string);
            }
        });
        xAxis.setLabel("Time");
        xAxis.setAnimated(false);


        // Setting up the Schedule chart object and their parents (containers)
        ScheduleChart<Number, String> scheduleChart = new ScheduleChart<>(xAxis, yAxis);

        // Setting up the height of the schedule chart
        scheduleChart.setBlockHeight(TASK_HEIGHT_DETERMINANT / _numProcessors);     

        return scheduleChart;
    }

    // Methods called when events are fired by nodes in the VisualisationDashboard.fxml
    /**
     * Switches between the light and dark themes
     */
    @FXML
    public void switchTheme() {
        _themeSwitcher.switchTheme();
    }

    /**
     * Minimises the visualisation module window
     */
    @FXML
    public void minimise() {
        stage.setIconified(true);
    }

    /**
     * Closes the visualisation module window
     */
    @FXML
    public void close() {
        shutdown();
    }

    /**
     * Exits the current thread when called
     */
    public void shutdown() {
        System.exit(0);
    }
}
