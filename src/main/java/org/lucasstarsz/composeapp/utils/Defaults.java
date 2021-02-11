package org.lucasstarsz.composeapp.utils;

import java.io.File;

public class Defaults {
    public static final String title = "Compose";
    public static final String fullTitle = title + " Text Editor";
    public static final String os = System.getProperty("os.name").startsWith("Win") ? "Windows" : System.getProperty("os.name").startsWith("Linux") ? "Linux" : "Mac";

    public static final String userHomeDir = String.join(File.separator, System.getProperty("user.home"), title);
    public static final String userPropertiesPath = String.join(File.separator, userHomeDir, "preferences", "prefs.properties");

    public static final String baseStylePath = "/css/base.css";
    public static final String iconPath = "/icons/compose_png.png";

    public static final String lightStylePath = "/css/themes/light.css";
    public static final String darcStylePath = "/css/themes/darc.css";
    public static final String freshStylePath = "/css/themes/fresh.css";

    public static final String mainFXMLPath = "main.fxml";
    public static final String settingsFXMLPath = "settings.fxml";

    public static final int width = 800;
    public static final int height = 600;
    public static final int minWidth = 400;
    public static final int minHeight = 150;

    public static final long fileSizeLimit = 100_000_000L;
    public static final String readableFileSizeLimit = (fileSizeLimit / 1_000_000L) + " megabytes";

    public static final long dragFileSizeLimit = 1_000_000L;
    public static final String readableDragFileSizeLimit = (dragFileSizeLimit / 1_000_000L) + " megabytes";
}
