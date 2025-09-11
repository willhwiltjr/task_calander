package storage;

import javax.swing.*;
import java.io.File;

public class EventDirectoryChooserDialog {

    /**
     * Prompts the user to select or create a directory for storing .ics files.
     * @param parent The parent JFrame (can be null).
     * @return The selected directory as a File, or null if cancelled.
     */
    public static File chooseDirectory(JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select or Create Events Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();

            // Create the directory if it doesn't exist
            if (!selectedDir.exists()) {
                boolean created = selectedDir.mkdirs();
                if (!created) {
                    JOptionPane.showMessageDialog(parent,
                            "Failed to create directory.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }

            return selectedDir;
        }

        return null; // User cancelled
    }
}
