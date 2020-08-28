package main.java.visualisation;

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
    private final ImageView _switchThemeImageView;

    /**
     * Constructor for ThemeSwitcher, which needs the scene to change the stylesheets for, the switchThemeButton
     * to change the image, and the initialCss which determines which theme is shown on startup.
     * @param scene the scene to switch the css for
     * @param switchThemeImageView the imageview to change the image for
     * @param initialCss which theme should be displayed initially
     */
    public ThemeSwitcher(Scene scene, ImageView switchThemeImageView, String initialCss) {
        _scene = scene;
        _switchThemeImageView = switchThemeImageView;
        setCss(initialCss);
    }

    /**
     * Toggles the theme of the scene depending on the current _theme.
     * Also changes the image of the _switchThemeImageView to what clicking it will do.
     */
    public void switchTheme() {
        if (_theme.equals("light")) {
            _theme = "dark";
            setCss(darkCss);
            _switchThemeImageView.setImage(darkImage);
        } else {
            _theme = "light";
            setCss(lightCss);
            _switchThemeImageView.setImage(lightImage);
        }
    }

    /**
     * Sets the current scene's css to the cssFile input.
     * @param cssFile contains the theme to apply
     */
    private void setCss(String cssFile) {
        _scene.getStylesheets().clear();
        _scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
    }
}
