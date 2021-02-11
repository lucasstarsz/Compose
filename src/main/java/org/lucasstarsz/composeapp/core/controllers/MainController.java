package org.lucasstarsz.composeapp.core.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import org.lucasstarsz.composeapp.core.ComposeApp;
import org.lucasstarsz.composeapp.nodes.FileTab;
import org.lucasstarsz.composeapp.nodes.FileTabPane;
import org.lucasstarsz.composeapp.utils.Defaults;
import org.lucasstarsz.composeapp.utils.DialogUtil;
import org.lucasstarsz.composeapp.utils.FileUtil;
import org.lucasstarsz.composeapp.utils.TextUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML private VBox mainContainer;

    @FXML private MenuItem newFileMenuItem;
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem saveAsMenuItem;
    @FXML private MenuItem closeCurrentMenuItem;
    @FXML private MenuItem closeAllMenuItem;
    @FXML private MenuItem closeComposeMenuItem;
    @FXML private MenuItem settingsMenuItem;

    @FXML private MenuItem undoMenuItem;
    @FXML private MenuItem redoMenuItem;
    @FXML private MenuItem cutMenuItem;
    @FXML private MenuItem copyMenuItem;
    @FXML private MenuItem pasteMenuItem;
    @FXML private MenuItem selectAllMenuItem;
    @FXML private MenuItem shiftLeftMenuItem;
    @FXML private MenuItem shiftRightMenuItem;
    @FXML private MenuItem findMenuItem;

    @FXML private FileTabPane fileTabs;

    @FXML
    public void initialize() {
        Map<MenuItem, KeyCombination> editMenuMnemonics = Map.of(
                undoMenuItem, new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.SHORTCUT_DOWN),
                redoMenuItem, new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.SHORTCUT_DOWN),
                copyMenuItem, new KeyCodeCombination(KeyCode.C, KeyCodeCombination.SHORTCUT_DOWN),
                cutMenuItem, new KeyCodeCombination(KeyCode.X, KeyCodeCombination.SHORTCUT_DOWN),
                pasteMenuItem, new KeyCodeCombination(KeyCode.V, KeyCodeCombination.SHORTCUT_DOWN),
                selectAllMenuItem, new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN),
                shiftLeftMenuItem, new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_DOWN),
                shiftRightMenuItem, new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHORTCUT_DOWN)
//                findMenuItem, new KeyCodeCombination(KeyCode.F, KeyCodeCombination.SHORTCUT_DOWN)
        );

        Map<MenuItem, KeyCombination> fileMenuMnemonics = Map.of(
                newFileMenuItem, new KeyCodeCombination(KeyCode.T, KeyCodeCombination.SHORTCUT_DOWN),
                openMenuItem, new KeyCodeCombination(KeyCode.O, KeyCodeCombination.SHORTCUT_DOWN),
                saveMenuItem, new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN),
                saveAsMenuItem, new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN, KeyCodeCombination.SHIFT_DOWN),
                closeCurrentMenuItem, new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
                closeAllMenuItem, new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
                closeComposeMenuItem, new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN),
                settingsMenuItem, new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN)
        );

        editMenuMnemonics.forEach(MenuItem::setAccelerator);
        fileMenuMnemonics.forEach(MenuItem::setAccelerator);

        initializeDrag();
    }

    private void initializeDrag() {
        mainContainer.setOnDragOver(dragEvent -> {
            if (dragEvent.getGestureSource() != mainContainer && dragEvent.getDragboard().hasFiles()) {
                dragEvent.acceptTransferModes(TransferMode.COPY);
            }
        });

        mainContainer.setOnDragDropped(dropEvent -> {
            Dragboard dragboard = dropEvent.getDragboard();
            boolean success = dragboard.hasFiles();

            dropEvent.setDropCompleted(success);
            dropEvent.consume();

            if (success) {
                List<File> filesToOpen = new ArrayList<>();

                dragboard.getFiles().forEach(file -> {
                    if (file.length() > Defaults.dragFileSizeLimit) {
                        DialogUtil.fileTooBig(file, Defaults.readableDragFileSizeLimit);
                        return;
                    }

                    if (!Files.isDirectory(file.toPath())) {
                        filesToOpen.add(file);
                    }
                });

                for (File file : filesToOpen) {
                    openFile(file);
                }
            }
        });
    }

    public void openFile(File file) {
        fileTabs.addTab(file);
    }

    @FXML
    private void openNewFile() {
        fileTabs.addNewTab();
    }

    @FXML
    private void openFileFromChooser() {
        FileTab currentTab = fileTabs.getCurrentFileTab();
        File file = FileUtil.tryGetFromChooser(currentTab.getCurrentFile());

        if (file != null) {
            ButtonType whereToOpenFile = DialogUtil.whereToOpen(file);

            if (whereToOpenFile.equals(DialogUtil.BUTTON_THIS_TAB)) {
                currentTab.setCurrentFile(file);
            } else if (whereToOpenFile.equals(DialogUtil.BUTTON_NEW_TAB)) {
                openFile(file);
            }
        }
    }

    @FXML
    private void closeCurrentFile() {
        fileTabs.closeCurrentTab();
    }

    @FXML
    private void closeAllFiles() {
        fileTabs.closeAllTabs();
    }

    @FXML
    private void closeEditor() {
        ComposeApp.getStage().fireEvent(ComposeApp.closeRequest);
    }

    @FXML
    private void openSettings() throws IOException {
        Dialog<Boolean> settingsDialog = DialogUtil.createEmptyDialog();
        settingsDialog.setTitle("Settings");

        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource(Defaults.settingsFXMLPath));
        settingsDialog.getDialogPane().setContent(settingsLoader.load());
        settingsDialog.showAndWait();
    }

    @FXML
    private void saveFile() throws IOException {
        fileTabs.getCurrentFileTab().saveFile();
    }

    @FXML
    private void saveFileAs() throws IOException {
        fileTabs.getCurrentFileTab().saveFileAs();
    }

    @FXML
    private void undo() {
        fileTabs.getCurrentFileTab().getTextArea().undo();
    }

    @FXML
    private void redo() {
        fileTabs.getCurrentFileTab().getTextArea().redo();
    }

    @FXML
    private void copy() {
        fileTabs.getCurrentFileTab().getTextArea().copy();
    }

    @FXML
    private void cut() {
        fileTabs.getCurrentFileTab().getTextArea().cut();
    }

    @FXML
    private void paste() {
        fileTabs.getCurrentFileTab().getTextArea().paste();
    }

    @FXML
    private void selectAll() {
        fileTabs.getCurrentFileTab().getTextArea().selectAll();
    }

    @FXML
    private void shiftRight() {
        TextUtil.shift(fileTabs.getCurrentFileTab().getTextArea(), 1);
    }

    @FXML
    private void shiftLeft() {
        TextUtil.shift(fileTabs.getCurrentFileTab().getTextArea(), -1);
    }

    // @TODO: Implement find, replace methods.
    @FXML
    private void find() {
    }

    public FileTabPane getFileTabPane() {
        return fileTabs;
    }
}
