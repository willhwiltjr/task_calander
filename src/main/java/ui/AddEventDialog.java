package ui;

import model.DateTimePickerUtil;
import model.LocalEvent;
import model.EventValidator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddEventDialog extends JDialog {
    private JTextField titleField;
    private JTextField startDateTimeField;
    private JTextField endDateTimeField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;

    private LocalEvent createdEvent;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AddEventDialog(JFrame parent) {
        this(parent, (LocalDateTime) null); // Default to now if no time is given
    }

    public AddEventDialog(JFrame parent, LocalDateTime suggestedStart) {
        super(parent, "Add New Event", true);
        setSize(450, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== Title =====
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx++;
        titleField = new JTextField(25);
        formPanel.add(titleField, gbc);

        // ===== Start DateTime =====
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Start Date & Time:"), gbc);
        gbc.gridx++;
        startDateTimeField = new JTextField(20);
        formPanel.add(startDateTimeField, gbc);
        attachDateTimePopup(startDateTimeField);

        // ===== End DateTime =====
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("End Date & Time:"), gbc);
        gbc.gridx++;
        endDateTimeField = new JTextField(20);
        formPanel.add(endDateTimeField, gbc);
        attachDateTimePopup(endDateTimeField);

        // ===== Description =====
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx++;
        descriptionArea = new JTextArea(4, 20);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set default values if suggested time is given
        if (suggestedStart != null) {
            startDateTimeField.setText(suggestedStart.format(FORMATTER));
            endDateTimeField.setText(suggestedStart.plusHours(1).format(FORMATTER));
        } else {
            LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
            startDateTimeField.setText(now.format(FORMATTER));
            endDateTimeField.setText(now.plusHours(1).format(FORMATTER));
        }

        // ===== Button Listeners =====
        cancelButton.addActionListener(e -> dispose());

        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                LocalDateTime start = LocalDateTime.parse(startDateTimeField.getText().trim(), FORMATTER);
                LocalDateTime end = LocalDateTime.parse(endDateTimeField.getText().trim(), FORMATTER);
                String desc = descriptionArea.getText().trim();

                String error = EventValidator.validate(title, start, end);
                if (error != null) {
                    JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (start.isAfter(end)) {
                    throw new IllegalArgumentException("Start time must be before end time.");
                }

                createdEvent = new LocalEvent(title, start, end, desc);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Optional: For editing existing event
    public AddEventDialog(JFrame parent, LocalEvent localEventToEdit) {
        this(parent, localEventToEdit != null ? localEventToEdit.getStartDateTime() : null);
        if (localEventToEdit != null) {
            titleField.setText(localEventToEdit.getTitle());
            startDateTimeField.setText(localEventToEdit.getStartDateTime().format(FORMATTER));
            endDateTimeField.setText(localEventToEdit.getEndDateTime().format(FORMATTER));
            descriptionArea.setText(localEventToEdit.getDescription());
        }
    }

    public LocalEvent getCreatedEvent() {
        return createdEvent;
    }

    /**
     * Attach a mini-calendar popup to a text field.
     * This uses a basic JOptionPane + JSpinner combo for simplicity.
     * You could replace this with a 3rd-party calendar component for a fancier UI.
     */
    private void attachDateTimePopup(JTextField targetField) {
        targetField.setEditable(false);
        targetField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        targetField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    LocalDateTime current = LocalDateTime.parse(targetField.getText(), FORMATTER);
                    LocalDateTime selected = DateTimePickerUtil.showDateTimePicker(AddEventDialog.this, current);
                    if (selected != null) {
                        targetField.setText(selected.format(FORMATTER));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AddEventDialog.this, "Invalid date format: " + ex.getMessage());
                }
            }
        });
    }

}


