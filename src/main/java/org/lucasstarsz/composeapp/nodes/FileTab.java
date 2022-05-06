package org.lucasstarsz.composeapp.nodes;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.lucasstarsz.composeapp.core.ComposeApp;
import org.lucasstarsz.composeapp.utils.DialogUtil;
import org.lucasstarsz.composeapp.utils.FileUtil;
import org.lucasstarsz.composeapp.utils.TextUtil;

public class FileTab extends ContentTab implements TextModifiable {
    private VirtualizedScrollPane<ComposeArea> scrollPane;
    private ComposeArea textArea;

    private String originalText;
    private boolean switchingFiles;

    public FileTab() {
        this("Untitled.txt");
    }

    private FileTab(String newFile) {
        super(newFile);
    }

    public FileTab(File file) {
        super(file);
    }

    @Override
    public void setupTabContents(File file) {
        originalText = "";
        textArea = new ComposeArea();
        scrollPane = new VirtualizedScrollPane<>(textArea);
        this.setContent(scrollPane);


        textArea.replaceText(originalText);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (unsavedChanges) {
                if (!textArea.isUndoAvailable() || textArea.getText().equals(originalText)) {
                    unsavedChanges = false;
                    this.setText(getCurrentFile().getName());
                }
            } else if (!switchingFiles) {
                unsavedChanges = true;
                this.setText(getCurrentFile().getName() + '*');
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

    @Override
    public void updateTabContents(File file) {
        try {
            switchingFiles = true;
            originalText = ComposeApp.readFileContents(file).get();
            switchingFiles = false;

            textArea.replaceText(originalText);
            textArea.getUndoManager().forgetHistory();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File writeTabContents(File file, boolean saveAs) throws IOException {
        if (saveAs) {
            return FileUtil.trySaveFileAs(textArea, file);
        } else {
            FileUtil.write(textArea, file);
            return null;
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

    @Override
    public void undo() {
        textArea.undo();
    }

    @Override
    public void redo() {
        textArea.redo();
    }

    @Override
    public void copy() {
        textArea.copy();
    }

    @Override
    public void cut() {
        textArea.cut();
    }

    @Override
    public void paste() {
        textArea.paste();
    }

    @Override
    public void selectAll() {
        textArea.selectAll();
    }

    public ComposeArea getTextArea() {
        return textArea;
    }
}
