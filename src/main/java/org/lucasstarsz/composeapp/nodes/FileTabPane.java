package org.lucasstarsz.composeapp.nodes;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import org.lucasstarsz.composeapp.user.Preferences;
import org.lucasstarsz.composeapp.utils.Defaults;
import org.lucasstarsz.composeapp.utils.DialogUtil;
import org.lucasstarsz.composeapp.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileTabPane extends TabPane {

    public FileTabPane() {
        super();
        this.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (this.getFileTabs().size() > 1) {
                this.getFileTabs().get(Math.max(newValue.intValue() - 1, 0)).getTextArea().requestFocus();
            }
        });
    }

    public void initNewTabTab() {
        Tab tab = new Tab("+");
        tab.setClosable(false);
        tab.setTooltip(new Tooltip("Creates a new file."));
        tab.setOnSelectionChanged(event -> addNewTab());
        this.getTabs().add(tab);
    }

    public FileTab getCurrentFileTab() {
        return (FileTab) this.getSelectionModel().getSelectedItem();
    }

    public List<FileTab> getFileTabs() {
        return getTabs().stream()
                .filter(tab -> tab instanceof FileTab)
                .map(tab -> (FileTab) tab)
                .collect(Collectors.toList());
    }

    public boolean closeAllTabs() {
        try {
            getFileTabs().forEach(tab -> {
                if (tab.shouldClose()) getTabs().remove(tab);
                else throw new BreakException();
            });
        } catch (BreakException ignored) {
            return false;
        }

        System.gc();

        return true;
    }

    public void closeCurrentTab() {
        FileTab currentFileTab = getCurrentFileTab();
        if (currentFileTab.shouldClose()) {
            getTabs().remove(currentFileTab);
        }
    }

    public void closeTab(int idx) {
        if (((FileTab) getTabs().get(idx)).shouldClose()) {
            getTabs().remove(idx);
        }
    }

    public void addTab(File file) {
        switch (FileUtil.validateFile(file)) {
            case VALID -> {
                FileTab tab = new FileTab(file);
                if (this.getTabs().size() == 0) {
                    this.getTabs().add(tab);
                } else {
                    this.getTabs().add(this.getTabs().size() - 1, tab);
                }

                this.getSelectionModel().select(tab);
                Platform.runLater(tab.getTextArea()::requestFocus);
            }
            case DOES_NOT_EXIST -> DialogUtil.doesNotExist(file.getAbsolutePath());
            case FAILED_TO_OPEN -> DialogUtil.cantOpenFile(file);
            case TOO_BIG -> DialogUtil.fileTooBig(file, Defaults.readableFileSizeLimit);
        }
    }

    public void addNewTab() {
        FileTab tab = new FileTab();
        this.getTabs().add(this.getTabs().size() - 1, tab);
        this.getSelectionModel().select(tab);
        Platform.runLater(tab.getTextArea()::requestFocus);
    }

    public void changeStyle(Preferences preferences) {
        String style = generateStyle(preferences);
        if (getTabs().size() > 50) {
            getTabs().parallelStream().forEach(tab -> {
                if (tab instanceof FileTab) {
                    tab.getContent().setStyle(style);
                    ((FileTab) tab).getTextArea().setWrapText(preferences.isWrapText());
                }
            });
        } else {
            getTabs().forEach(tab -> {
                if (tab instanceof FileTab) {
                    tab.getContent().setStyle(style);
                    ((FileTab) tab).getTextArea().setWrapText(preferences.isWrapText());
                }
            });
        }
    }

    private String generateStyle(Preferences preferences) {
        return "-fx-font-family:" + preferences.getFontName() + "; -fx-font-size:" + preferences.getFontSize() + ";";
    }
}
