package org.lucasstarsz.composeapp.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.lucasstarsz.composeapp.core.ComposeApp;
import org.lucasstarsz.composeapp.nodes.FileTab;

import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class DialogUtil {

    public static final ButtonType BUTTON_THIS_TAB = new ButtonType("This tab");
    public static final ButtonType BUTTON_NEW_TAB = new ButtonType("New tab");

    public static ButtonType whereToOpen(File file) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(ComposeApp.getStage());

        alert.setTitle("Open file");
        alert.setHeaderText("");
        alert.setContentText("Where would you like to open " + file.getName() + "?");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(BUTTON_THIS_TAB, BUTTON_NEW_TAB);

        AtomicReference<ButtonType> confirmation = new AtomicReference<>(null);
        alert.showAndWait().ifPresent(confirmation::set);

        return confirmation.get();
    }

    /**
     * Confirms that the user wants to overwrite the file they're currently editing.
     *
     * @param tab The tab to be closed.
     * @return The user's decision, to either proceed or cancel the operation.
     */
    public static boolean confirmUnsavedChanges(FileTab tab, String reason) {
        Alert confirmUnsavedAlert = createWarningAlert();

        confirmUnsavedAlert.setTitle("Unsaved Changes");
        confirmUnsavedAlert.setContentText(
                "You have unsaved changes in " + tab.getText() + "."
                        + System.lineSeparator()
                        + "Are you sure you want to " + reason + "?"
        );

        return checkConfirmation(confirmUnsavedAlert);
    }

    public static void doesNotExist(String filePath) {
        Alert cantOpenFileAlert = createErrorAlert();

        cantOpenFileAlert.setTitle("File doesn't exist");
        cantOpenFileAlert.setContentText("The file at " + filePath + " does not exist.");

        cantOpenFileAlert.showAndWait();
    }

    /**
     * Show error dialogue, displaying failure to open the file at the specified path.
     *
     * @param file The file that failed to open.
     */
    public static void cantOpenFile(File file) {
        Alert cantOpenFileAlert = createErrorAlert();

        cantOpenFileAlert.setTitle("Can't open file");
        cantOpenFileAlert.setContentText("Sorry, I couldn't open the file at " + file.getAbsolutePath() + ".");

        cantOpenFileAlert.showAndWait();
    }

    public static void fileTooBig(File file, String readableLimit) {
        Alert cantOpenFileAlert = createErrorAlert();

        cantOpenFileAlert.setTitle("File too big");
        cantOpenFileAlert.setContentText("The file at " + file.getAbsolutePath() + " exceeds the file size limit of " + readableLimit + ".");

        cantOpenFileAlert.showAndWait();
    }

    /**
     * Creates a warning alert with {@link ButtonType}{@code .NO} and {@link ButtonType}{@code .YES}.
     *
     * @return The newly created warning alert.
     */
    private static Alert createWarningAlert() {
        Alert confirmationAlert = new Alert(Alert.AlertType.WARNING);
        confirmationAlert.initModality(Modality.APPLICATION_MODAL);
        confirmationAlert.initOwner(ComposeApp.getStage());

        confirmationAlert.setHeaderText("");

        confirmationAlert.getButtonTypes().clear();
        confirmationAlert.getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
        return confirmationAlert;
    }

    /**
     * Creates an error alert with default button implementation.
     *
     * @return The newly created error alert.
     */
    private static Alert createErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.initModality(Modality.APPLICATION_MODAL);
        errorAlert.initOwner(ComposeApp.getStage());

        errorAlert.setHeaderText("");

        return errorAlert;
    }

    public static Dialog<Boolean> createEmptyDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(ComposeApp.getStage());

        dialog.setHeaderText("");

        Window resultWindow = dialog.getDialogPane().getScene().getWindow();
        resultWindow.setOnCloseRequest(event -> resultWindow.hide());

        return dialog;
    }

    /**
     * Checks user decision on {@link Alert} of alert type {@link Alert.AlertType}{@code .WARNING}.
     *
     * @param confirmationAlert The {@link Alert} to check.
     * @return The user's decision, to either proceed or cancel the operation.
     */
    private static boolean checkConfirmation(Alert confirmationAlert) {
        assert confirmationAlert.getAlertType() == Alert.AlertType.WARNING;

        AtomicReference<Boolean> confirmation = new AtomicReference<>(false);
        Toolkit.getDefaultToolkit().beep();

        confirmationAlert.showAndWait().ifPresent(button -> {
            if (button == ButtonType.YES) {
                confirmation.set(true);
            } else if (button == ButtonType.NO) {
                confirmation.set(false);
            }
        });

        return confirmation.get();
    }
}
