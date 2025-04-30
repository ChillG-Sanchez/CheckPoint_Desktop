package org.redible.checkpoint.checkpoint;

public class AppConfig {
    private static boolean isDarkModeEnabled = false;

    public static boolean isDarkModeEnabled() {
        return isDarkModeEnabled;
    }

    public static void setDarkModeEnabled(boolean enabled) {
        isDarkModeEnabled = enabled;
    }
}
