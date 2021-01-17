package org.lucasstarsz.composeapp.user;

import javafx.stage.Stage;
import org.lucasstarsz.composeapp.core.ComposeApp;
import org.lucasstarsz.composeapp.utils.Defaults;
import org.lucasstarsz.composeapp.utils.PreferencesUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

// TODO: work with preferences
public class Preferences {
    private final String pathToProperties;
    private final Properties properties;

    private String themePath;
    private String themeName;
    private String fontName;

    private int fontSize;
    private int tabSize;
    private boolean wrapText;

    public Preferences(String path) throws IOException {
        pathToProperties = path;
        properties = PreferencesUtil.get(pathToProperties);
    }

    public void populate() {
        setTheme(properties.getProperty("theme"));
        setFontName(properties.getProperty("font"));
        setFontSize(Integer.parseInt(properties.getProperty("fontsize")));
        setWrapText(Boolean.parseBoolean(properties.getProperty("wraptext")));
        setTabSize(8);
    }

    public void apply(Stage stage) throws IOException {
        String baseCSS = getClass().getResource(Defaults.baseStylePath).toExternalForm();
        String themeCSS = getClass().getResource(themePath).toExternalForm();

        stage.getScene().getStylesheets().clear();
        stage.getScene().getStylesheets().addAll(baseCSS, themeCSS);
        ComposeApp.getMainController().getFileTabPane().changeStyle(this);

        save();
    }

    public void save() throws IOException {
        try (FileOutputStream out = new FileOutputStream(pathToProperties)) {
            properties.setProperty("theme", themePath);
            properties.setProperty("font", fontName);
            properties.setProperty("fontsize", String.valueOf(fontSize));
            properties.setProperty("wraptext", String.valueOf(wrapText));
            properties.store(out, null);
        }
    }

    public void setTheme(String newTheme) {
        themePath = newTheme;
        themeName = switch (themePath) {
            case Defaults.darcStylePath -> "Dark";
            case Defaults.lightStylePath -> "Light";
            case Defaults.freshStylePath -> "Fresh";
            default -> "Custom Theme";
        };
    }

    public void setFontName(String newFontName) {
        fontName = newFontName;
    }

    public void setFontSize(int newFontSize) {
        fontSize = newFontSize;
    }

    public void setTabSize(int newTabSize) {
        tabSize = newTabSize;
    }

    public void setWrapText(boolean shouldWrapText) {
        wrapText = shouldWrapText;
    }

    public String getThemePath() {
        return themePath;
    }

    public String getThemeName() {
        return themeName;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getTabSize() {
        return tabSize;
    }

    public boolean isWrapText() {
        return wrapText;
    }
}
