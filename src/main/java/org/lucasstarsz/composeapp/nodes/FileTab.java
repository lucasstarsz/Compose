package org.lucasstarsz.composeapp.nodes;

import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.lucasstarsz.composeapp.core.ComposeApp;
import org.lucasstarsz.composeapp.utils.Defaults;
import org.lucasstarsz.composeapp.utils.DialogUtil;
import org.lucasstarsz.composeapp.utils.FileUtil;
import org.lucasstarsz.composeapp.utils.TextUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class FileTab extends Tab {
    private final VirtualizedScrollPane<ComposeArea> scrollPane;
    private final ComposeArea textArea;

    private File currentFile;
    private String originalText;

    private boolean unsavedChanges;
    private boolean switchingFiles;

    public FileTab() {
        this("Untitled.txt");
    }

    private FileTab(String newFile) {
        switchingFiles = true;
        currentFile = new File(newFile);
        originalText = "";

        textArea = new ComposeArea();
        scrollPane = new VirtualizedScrollPane<>(textArea);
        this.setContent(scrollPane);

        setup();
        switchingFiles = false;
    }

    public FileTab(File file) {
        switchingFiles = true;
        originalText = "";

        textArea = new ComposeArea();
        scrollPane = new VirtualizedScrollPane<>(textArea);
        this.setContent(scrollPane);

        setCurrentFile(file);
        setup();
        switchingFiles = false;
    }

    private void setup() {
        this.setText(currentFile.getName());

        textArea.replaceText(originalText);

        this.setOnCloseRequest(event -> {
            if (shouldClose()) {
                this.getTabPane().getTabs().remove(this);
            }
        });

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (unsavedChanges) {
                if (!textArea.isUndoAvailable() || textArea.getText().equals(originalText)) {
                    unsavedChanges = false;
                    this.setText(currentFile.getName());
                }
            } else if (!switchingFiles) {
                unsavedChanges = true;
                this.setText(currentFile.getName() + '*');
            }
        });

        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                if (event.isShiftDown()) {
                    TextUtil.shift(textArea, -1);
                } else if (event.isShortcutDown()) {
                    TextUtil.shift(textArea, 1);
                } else {
                    textArea.insertText(textArea.getCaretPosition(), "\t");
                }

                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                TextUtil.enterWithTabs(textArea, event);
            }
        });
    }

    public void setCurrentFile(File file) {
        switch (FileUtil.validateFile(file)) {
            case VALID -> {
                if (!unsavedChanges || DialogUtil.confirmUnsavedChanges(this, "open " + file.getName())) {
                    try {
                        switchingFiles = true;

                        originalText = ComposeApp.readFileContents(file).get();

                        currentFile = file;
                        this.setText(currentFile.getName());
                        textArea.replaceText(originalText);
                        textArea.getUndoManager().forgetHistory();

                        unsavedChanges = false;
                        switchingFiles = false;
                    } catch (InterruptedException | ExecutionException ignored) {}
                }
            }
            case DOES_NOT_EXIST -> DialogUtil.doesNotExist(file.getAbsolutePath());
            case FAILED_TO_OPEN -> DialogUtil.cantOpenFile(file);
            case TOO_BIG -> DialogUtil.fileTooBig(file, Defaults.readableFileSizeLimit);
        }
    }

    public void saveFile() throws IOException {
        if (currentFile.exists()) {
            FileUtil.write(textArea, currentFile);
            setCurrentFile(currentFile);
        } else {
            saveFileAs();
        }
    }

    public void saveFileAs() throws IOException {
        File f = FileUtil.trySaveFileAs(textArea, currentFile);
        if (f != null) {
            setCurrentFile(f);
        }
    }

    public void setFont(String fontStyle) {
        this.getContent().setStyle(fontStyle);
    }

    public void setTabSize(String tabSizeStyle) {
        this.getContent().setStyle(tabSizeStyle);
    }

    public boolean shouldClose() {
        return !unsavedChanges || DialogUtil.confirmUnsavedChanges(this, "close this tab");
    }

    public VirtualizedScrollPane<ComposeArea> getScrollPane() {
        return scrollPane;
    }

    public ComposeArea getTextArea() {
        return textArea;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }
}
