package model;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateTimePickerUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Opens a date/time picker dialog and returns the selected LocalDateTime.
     *
     * @param parent     The parent component (usually the dialog or frame)
     * @param initial    The initial value to display
     * @return           The selected LocalDateTime, or null if canceled
     */
    public static LocalDateTime showDateTimePicker(Component parent, LocalDateTime initial) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));

        SpinnerDateModel model = new SpinnerDateModel(
                initial != null ? java.sql.Timestamp.valueOf(initial) : new Date(),
                null,
                null,
                Calendar.MINUTE
        );

        JSpinner dateTimeSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateTimeSpinner, "yyyy-MM-dd HH:mm");
        dateTimeSpinner.setEditor(editor);

        panel.add(new JLabel("Select date & time:"));
        panel.add(dateTimeSpinner);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Pick Date & Time", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Date selected = model.getDate();
            return new java.sql.Timestamp(selected.getTime()).toLocalDateTime();
        }

        return null;
    }
}

