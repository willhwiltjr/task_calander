package storage;



import javax.swing.*;
import java.io.File;
import java.util.prefs.Preferences;

public class DirectoryManager {
    private static final String PREF_KEY = "events_directory";
    private static final Preferences prefs = Preferences.userRoot().node("scheduler");

    /**
     * Gets the saved directory path. If not found, prompts the user.
     */
    public static File getOrChooseDirectory(JFrame parent) {
        String savedPath = prefs.get(PREF_KEY, null);

        if (savedPath != null) {
            File savedDir = new File(savedPath);
            if (savedDir.exists() && savedDir.isDirectory()) {
                return savedDir;
            }
        }

        // No valid saved directory, prompt user
        File chosenDir = EventDirectoryChooserDialog.chooseDirectory(parent);
        if (chosenDir != null) {
            prefs.put(PREF_KEY, chosenDir.getAbsolutePath());
        }

        return chosenDir;
    }

    /**
     * Manually clear the saved directory (if needed).
     */
    public static void clearSavedDirectory() {
        prefs.remove(PREF_KEY);
    }
}

