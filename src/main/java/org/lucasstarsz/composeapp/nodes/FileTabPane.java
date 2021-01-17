package org.lucasstarsz.composeapp.nodes;

import javafx.scene.control.TabPane;
import org.lucasstarsz.composeapp.user.Preferences;

import java.util.ArrayList;
import java.util.Collections;
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

    public void setFont(String fontName, double fontSize) {
        String fontStyle = "-fx-font-family:" + fontName + "; -fx-font-size:" + fontSize + ";";
        getTabs().forEach(tab -> {
            if (tab instanceof FileTab) {
                ((FileTab) tab).setFont(fontStyle);
            }
        });
    }

    public void setTabSize(int tabSize) {
        String tabSizeStyle = "-fx-tab-size: " + tabSize;
        getTabs().forEach(tab -> {
            if (tab instanceof FileTab) {
                ((FileTab) tab).setTabSize(tabSizeStyle);
            }
        });
    }

    public void setWrapText(boolean wrapText) {
        getTabs().forEach(tab -> {
            if (tab instanceof FileTab) {
                ((FileTab) tab).getTextArea().setWrapText(wrapText);
            }
        });
    }

    public void changeStyle(Preferences preferences) {
        String style = generateStyle(preferences);
        getTabs().forEach(tab -> {
            if (tab instanceof FileTab) {
                tab.getContent().setStyle(style);
                ((FileTab) tab).getTextArea().setWrapText(preferences.isWrapText());
            }
        });
    }

    private String generateStyle(Preferences preferences) {
        return "-fx-font-family:" + preferences.getFontName() + "; -fx-font-size:" + preferences.getFontSize() + ";";
    }
}
