package org.lucasstarsz.composeapp.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.lucasstarsz.composeapp.core.controllers.MainController;
import org.lucasstarsz.composeapp.user.Preferences;
import org.lucasstarsz.composeapp.utils.Defaults;
import org.lucasstarsz.composeapp.utils.DialogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ComposeApp extends Application {

    public static Event closeRequest = new WindowEvent(ComposeApp.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST);
    private static String openOnStart = null;

    private static Stage mainStage;
    private static MainController mainController;
    private static Preferences preferences;

    private static final ExecutorService fileReader = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static Stage getStage() {
        return mainStage;
    }

    public static Preferences getPreferences() {
        return preferences;
    }

    public static MainController getMainController() {
        return mainController;
    }

    @SuppressWarnings("ReadWriteStringCanBeUsed")
    public static Future<String> readFileContents(File file) {
        return fileReader.submit(() -> {
            if (Files.isReadable(file.toPath())) {
                try {
                    return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    DialogUtil.cantOpenFile(file);
                }
            }

            return null;
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Platform.setImplicitExit(false);
            mainStage = stage;

            createStage();

            if (openOnStart != null) {
                mainController.openFile(new File(openOnStart));
            }
            mainController.getFileTabPane().initNewTabTab();

            styleStage();
            mainStage.show();

            System.gc();

        } catch (Exception e) {
            File log = new File(Defaults.userHomeDir + File.separator + "errorlog.txt");
            String[] result = new String[e.getStackTrace().length + 1];
            String[] error = Arrays.stream(e.getStackTrace()).map(Object::toString).toArray(String[]::new);

            /* Format the error log. */
            String dashes = "-".repeat(10);
            result[0] = dashes + " ERROR LOGFILE " + dashes
                    + System.lineSeparator().repeat(2)
                    + e.getLocalizedMessage();
            System.arraycopy(error, 0, result, 1, result.length - 1);

            /* Write the error to the error log. */
            Files.write(log.toPath(), Collections.singleton(String.join(System.lineSeparator(), result)));

            /* Notify the user. */
            System.err.println(
                    "An error occurred while trying to run " + Defaults.title + "."
                            + System.lineSeparator()
                            + " A logfile was produced at: " + log.getAbsolutePath()
            );

            /* Exit */
            Platform.exit();
            System.exit(-1);
        }
    }

    private void createStage() throws IOException {
        FXMLLoader mainFXML = new FXMLLoader(getClass().getResource(Defaults.mainFXMLPath));
        Parent mainContent = mainFXML.load();
        mainController = mainFXML.getController();

        Scene mainScene = new Scene(mainContent, Defaults.width, Defaults.height);
        mainStage.setScene(mainScene);
        mainStage.setTitle(Defaults.fullTitle);
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream(Defaults.iconPath)));

        mainStage.setOnCloseRequest(closeRequest -> {
            if (mainController.getFileTabPane().closeAllTabs()) {
                Platform.exit();
                System.exit(0);
            } else {
                closeRequest.consume();
            }
        });
    }

    private void styleStage() throws IOException {
        mainStage.getScene().getStylesheets().add(getClass().getResource(Defaults.baseStylePath).toExternalForm());

        mainStage.setMinWidth(Defaults.minWidth);
        mainStage.setMinHeight(Defaults.minHeight);

        preferences = new Preferences(Defaults.userPropertiesPath);
        preferences.populate();
        preferences.apply(mainStage);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            openOnStart = String.join(" ", Arrays.stream(args)
                    .filter(file -> !file.startsWith("-D"))
                    .filter(file -> !file.equals("org.lucasstarsz.composeapp.core.ComposeApp"))
                    .toArray(String[]::new));
            if (openOnStart.length() == 0) {
                openOnStart = null;
            }
        }

        launch(args);
    }
}
