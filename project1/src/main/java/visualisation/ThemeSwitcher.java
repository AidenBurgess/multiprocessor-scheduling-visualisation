package main.java.visualisation;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Handles switching of themes for the main window.
 * Can switch between light and dark theme.
 */
public class ThemeSwitcher {
    // Constants for location of css files
    private static final String lightCss = "css/light-style.css";
    private static final String darkCss = "css/dark-style.css";
    // Constants for button icon images
    private static final Image lightImage = new Image("main/java/visualisation/icons/light-theme.png");
    private static final Image darkImage = new Image("main/java/visualisation/icons/dark-theme.png");

    private String _theme = "light";
    private final Scene _scene;
    private final ImageView _switchThemeButton;

    /**
     * Constructor for ThemeSwitcher, which needs the scene to change the stylesheets for, the switchThemeButton
     * to change the image, and the initialCss which determines which theme is shown on startup.
     * @param scene
     * @param switchThemeButton
     * @param initialCss
     */
    public ThemeSwitcher(Scene scene, ImageView switchThemeButton, String initialCss) {
        _scene = scene;
        _switchThemeButton = switchThemeButton;
        setCss(initialCss);
    }

    /**
     * Toggles the theme of the scene depending on the current _theme.
     * Also changes the image of the _switchThemeButton to what clicking it will do.
     */
    public void switchTheme() {
        if (_theme.equals("light")) {
            _theme = "dark";
            setCss(darkCss);
            _switchThemeButton.setImage(darkImage);
        } else {
            _theme = "light";
            setCss(lightCss);
            _switchThemeButton.setImage(lightImage);
        }
    }

    /**
     * Sets the current scene's css to the cssFile input.
     * @param cssFile contains the theme to apply
     */
    private void setCss(String cssFile) {
        Platform.runLater(() -> {
            _scene.getStylesheets().clear();
            _scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
        });
    }
}
