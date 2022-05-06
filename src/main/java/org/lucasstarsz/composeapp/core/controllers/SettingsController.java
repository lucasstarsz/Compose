package org.lucasstarsz.composeapp.core.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import org.lucasstarsz.composeapp.core.ComposeApp;
import org.lucasstarsz.composeapp.user.Preferences;
import org.lucasstarsz.composeapp.utils.Defaults;

public class SettingsController {

    @FXML
    private ComboBox<String> themeSelector;

    @FXML
    private ComboBox<String> fontSelector;
    @FXML
    private Label fontSizeText;
    @FXML
    private TextField fontSizeField;

//    @FXML private Label tabSizeText;
//    @FXML private TextField tabSizeField;

    @FXML
    private CheckBox wrapTextSelector;

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

//        tabSizeText.setText("Tab Size");
//        tabSizeField.setPromptText("Current Tab Size: " + ComposeApp.getPreferences().getTabSize());

        wrapTextSelector.setText("Wrap Text");
        wrapTextSelector.setSelected(ComposeApp.getPreferences().isWrapText());
    }

    @FXML
    private void setTheme() {
        ComposeApp.getPreferences().setTheme(themes.get(themeSelector.getSelectionModel().getSelectedItem()));
        Preferences.reapply();
        themeSelector.setPromptText("Set Theme (Current: " + ComposeApp.getPreferences().getThemeName() + ")");
    }

    @FXML
    private void setFont() {
        ComposeApp.getPreferences().setFontName(fontSelector.getSelectionModel().getSelectedItem());
        Preferences.reapply();
        fontSelector.setPromptText("Set Font (Current: " + ComposeApp.getPreferences().getFontName() + ")");
    }

    @FXML
    private void setFontSize() {
        try {
            int fontSize = Integer.parseInt(fontSizeField.getText());
            ComposeApp.getPreferences().setFontSize(fontSize);
            Preferences.reapply();
            fontSizeField.setPromptText("Current Font Size: " + ComposeApp.getPreferences().getFontSize());
        } catch (NumberFormatException ignored) {
        }
    }

//    @FXML
//    private void setTabSize() {
//        try {
//            int tabSize = Integer.parseInt(tabSizeField.getText());
//            ComposeApp.getPreferences().setTabSize(tabSize);
//            Preferences.reapply();
//        } catch (NumberFormatException ignored) {
//        }
//    }

    @FXML
    private void setWrapped() {
        ComposeApp.getPreferences().setWrapText(wrapTextSelector.isSelected());
        Preferences.reapply();
    }
}
