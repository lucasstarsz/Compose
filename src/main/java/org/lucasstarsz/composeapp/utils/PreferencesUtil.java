package org.lucasstarsz.composeapp.utils;

import java.io.*;
import java.util.Properties;

public class PreferencesUtil {
    public static Properties get(String location) throws IOException {
        Properties result;

        try (FileInputStream inputStream = new FileInputStream(location)) {
            result = new Properties();
            result.load(inputStream);
        } catch (FileNotFoundException e) {
            generateProperties(location);
            return get(location);
        }

        return result;
    }

    private static void generateProperties(String location) throws IOException {
        File prefsFile = new File(location);
        File prefsDir = new File(prefsFile.getAbsoluteFile().getParent());

        if (prefsDir.mkdirs() || prefsFile.createNewFile()) {
            try (FileWriter fw = new FileWriter(prefsFile)) {
                fw.write(
                        "theme=" + Defaults.darcStylePath
                                + System.lineSeparator()
                                + "font=Inconsolata"
                                + System.lineSeparator()
                                + "fontsize=16"
                                + System.lineSeparator()
                                + "wraptext=true"
                );
            }
        }
    }
}
