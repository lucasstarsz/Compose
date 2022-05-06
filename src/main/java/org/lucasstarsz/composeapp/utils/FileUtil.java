package org.lucasstarsz.composeapp.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import javafx.stage.FileChooser;
import org.lucasstarsz.composeapp.core.ComposeApp;
import org.lucasstarsz.composeapp.nodes.ComposeArea;

public class FileUtil {

    private static final FileChooser.ExtensionFilter textFileFilter = new FileChooser.ExtensionFilter("Text File", "*.txt");
    private static final FileChooser.ExtensionFilter allFilesFiler = new FileChooser.ExtensionFilter("All Files", "*.*");

    public enum FileValidationStatus {
        VALID,
        DOES_NOT_EXIST,
        TOO_BIG,
        FAILED_TO_OPEN
    }

    /**
     * Tries to open a new file, and returns the result.
     *
     * @param currentFile The current file.
     * @return The new file, with the possibility of being null.
     */
    public static File tryGetFromChooser(File currentFile) {
        FileChooser chooser = createFileChooser(currentFile);
        return chooser.showOpenDialog(ComposeApp.getStage());
    }

    /**
     * Tries to save the specified file, returning the file that may have bene written to.
     *
     * @param textArea The container of the content to write.
     * @param file     The file to write to.
     * @return The file that was written to.
     * @throws IOException This is thrown by {@code FileUtil.write(...)} if there is an error when writing the file.
     */
    public static File trySaveFileAs(ComposeArea textArea, File file) throws IOException {
        FileChooser chooser = createFileChooser(file);
        File saveFile = chooser.showSaveDialog(ComposeApp.getStage());
        if (saveFile != null) {
            FileUtil.write(textArea, saveFile);
        }
        return saveFile;
    }

    /**
     * Writes the contents of the editor to the specified file.
     *
     * @param textArea The container of the content to write.
     * @param file     The file to write to.
     * @throws IOException This is thrown by {@code new FileWriter(...), FileWriter#write(...)} if there is an error
     *                     when creating the {@link FileWriter} or while writing the file.
     */
    public static void write(ComposeArea textArea, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile())) {
            fw.write(textArea.getText());
        }
    }

    /**
     * Creates a {@link FileChooser}, starting in the directory of the specified file.
     *
     * @param file The container of the initial directory.
     * @return The created {@link FileChooser}.
     */
    private static FileChooser createFileChooser(File file) {
        FileChooser chooser = new FileChooser();
        if (file != null && file.exists()) {
            chooser.setInitialDirectory(new File(file.getAbsoluteFile().getParent()));
        }

        chooser.getExtensionFilters().addAll(textFileFilter, allFilesFiler);
        return chooser;
    }

    public static FileValidationStatus validateFile(File file) {
        if (!Files.exists(file.toPath())) {
            return FileValidationStatus.DOES_NOT_EXIST;
        }

        try {
            if (Files.size(file.toPath()) > Defaults.fileSizeLimit) {
                return FileValidationStatus.TOO_BIG;
            } else {
                return FileValidationStatus.VALID;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return FileValidationStatus.FAILED_TO_OPEN;
        }
    }
}
