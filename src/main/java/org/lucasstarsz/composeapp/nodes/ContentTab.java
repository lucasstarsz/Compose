package org.lucasstarsz.composeapp.nodes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javafx.scene.control.Tab;
import org.lucasstarsz.composeapp.utils.Defaults;
import org.lucasstarsz.composeapp.utils.DialogUtil;
import org.lucasstarsz.composeapp.utils.FileUtil;

public abstract class ContentTab extends Tab {

    private File currentFile;
    protected boolean unsavedChanges;

    protected ContentTab(String newFile) {
        currentFile = new File(newFile);
        setup();
    }

    protected ContentTab(File file) {
        setCurrentFile(file);
        setup();
    }

    private void setup() {
        this.setText(currentFile.getName());
        this.setOnCloseRequest(event -> {
            if (shouldClose()) {
                this.getTabPane().getTabs().remove(this);
            }
        });
        setupTabContents(currentFile);
    }

    public abstract void setupTabContents(File file);

    public abstract void updateTabContents(File file);

    public abstract File writeTabContents(File file, boolean saveAs) throws IOException;

    public void setCurrentFile(File file) {
        switch (FileUtil.validateFile(file)) {
            case VALID -> {
                if (!unsavedChanges || DialogUtil.confirmUnsavedChanges(this, "open " + file.getName())) {
                    if (currentFile == null) {
                        currentFile = file;
                    } else {
                        currentFile = file;
                        updateTabContents(currentFile);
                    }
                    this.setText(currentFile.getName());
                    unsavedChanges = false;
                }
            }
            case DOES_NOT_EXIST -> DialogUtil.doesNotExist(file.getAbsolutePath());
            case FAILED_TO_OPEN -> DialogUtil.cantOpenFile(file);
            case TOO_BIG -> DialogUtil.fileTooBig(file, Defaults.readableFileSizeLimit);
        }
    }

    public void saveFile() throws IOException {
        if (Files.exists(currentFile.toPath())) {
            writeTabContents(currentFile, false);
            System.out.println("Saved: " + currentFile.getAbsolutePath());
        } else {
            saveFileAs();
        }
    }

    public void saveFileAs() throws IOException {
        File f = writeTabContents(currentFile, true);
        if (f != null) {
            String oldFileName = currentFile.getAbsolutePath();
            setCurrentFile(f);
            System.out.println("Saved: " + oldFileName + " as: " + f.getAbsolutePath());
        }
    }

    public boolean shouldClose() {
        return !unsavedChanges || DialogUtil.confirmUnsavedChanges(this, "close this tab");
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }
}
