package org.lucasstarsz.composeapp.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import org.lucasstarsz.composeapp.core.ComposeApp;
import org.lucasstarsz.composeapp.utils.Defaults;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SettingsController {

    @FXML private ComboBox<String> themeSelector;

    @FXML private ComboBox<String> fontSelector;
    @FXML private Label fontSizeText;
    @FXML private TextField fontSizeField;

    @FXML private Label tabSizeText;
    @FXML private TextField tabSizeField;

    @FXML private CheckBox wrapTextSelector;

    private final Map<String, String> themes = Map.of(
            "Light", Defaults.lightStylePath,
            "Fresh", Defaults.freshStylePath,
            "Darc", Defaults.darcStylePath
    );

    @FXML
    private void initialize() {
        themeSelector.setPromptText("Set Theme (Current: " + ComposeApp.getPreferences().getThemeName() + ")");

        List<String> availableFonts = Font.getFamilies();
        availableFonts.sort(Comparator.naturalOrder());

        fontSelector.setPromptText("Set Font (Current: " + ComposeApp.getPreferences().getFontName() + ")");
        fontSelector.getItems().addAll(availableFonts);

        fontSizeText.setText("Font Size");
        fontSizeField.setPromptText("Current Font Size: " + ComposeApp.getPreferences().getFontSize());

        tabSizeText.setText("Tab Size");
        tabSizeField.setPromptText("Current Tab Size: " + ComposeApp.getPreferences().getTabSize());

        wrapTextSelector.setText("Wrap Text");
        wrapTextSelector.setSelected(ComposeApp.getPreferences().isWrapText());
    }

    @FXML
    private void setTheme() throws IOException {
        ComposeApp.getPreferences().setTheme(themes.get(themeSelector.getSelectionModel().getSelectedItem()));
        ComposeApp.getPreferences().apply(ComposeApp.getStage());
    }

    @FXML
    private void setFont() throws IOException {
        ComposeApp.getPreferences().setFontName(fontSelector.getSelectionModel().getSelectedItem());
        ComposeApp.getPreferences().apply(ComposeApp.getStage());
    }

    @FXML
    private void setFontSize() throws IOException {
        try {
            int fontSize = Integer.parseInt(fontSizeField.getText());
            ComposeApp.getPreferences().setFontSize(fontSize);
            ComposeApp.getPreferences().apply(ComposeApp.getStage());
        } catch (NumberFormatException ignored) {
        }
    }

    @FXML
    private void setTabSize() throws IOException {
        try {
            int tabSize = Integer.parseInt(tabSizeField.getText());
            ComposeApp.getPreferences().setTabSize(tabSize);
            ComposeApp.getPreferences().apply(ComposeApp.getStage());
        } catch (NumberFormatException ignored) {
        }
    }

    @FXML
    private void setWrapped() throws IOException {
        ComposeApp.getPreferences().setWrapText(wrapTextSelector.isSelected());
        ComposeApp.getPreferences().apply(ComposeApp.getStage());
    }
}
