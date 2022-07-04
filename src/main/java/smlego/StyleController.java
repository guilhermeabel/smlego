package smlego;

import javafx.scene.layout.Pane;

public class StyleController {
    final static String CSS_FILENAME = "theme/style.css";
    final static String LIGHT_THEME = "lightTheme";
    final static String HIGH_CONTRAST_THEME = "highContrastTheme";
    private static String currentTheme = LIGHT_THEME;

    public String getStylesheetFile() {
        return getClass().getResource(CSS_FILENAME).toExternalForm();
    }

    public static void setMainStyle(Pane mainPane) {
        if (currentTheme == LIGHT_THEME) {
            mainPane.setId(currentTheme = HIGH_CONTRAST_THEME);
        } else if (currentTheme == HIGH_CONTRAST_THEME) {
            mainPane.setId(currentTheme = LIGHT_THEME);
        }
    }
}
