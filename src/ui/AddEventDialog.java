package ui;

import model.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class AddEventDialog extends JDialog {
    private JTextField titleField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;

    private Event createdEvent;

    public AddEventDialog(JFrame parent) {
        super(parent, "Add New Event", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // === Form Panel ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx++;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Start Time (HH:mm):"), gbc);
        gbc.gridx++;
        startTimeField = new JTextField("09:00", 10);
        formPanel.add(startTimeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("End Time (HH:mm):"), gbc);
        gbc.gridx++;
        endTimeField = new JTextField("10:00", 10);
        formPanel.add(endTimeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx++;
        descriptionArea = new JTextArea(4, 20);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        add(formPanel, BorderLayout.CENTER);

        // === Button Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // === Button Listeners ===
        cancelButton.addActionListener(e -> dispose());

        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                LocalTime start = LocalTime.parse(startTimeField.getText().trim());
                LocalTime end = LocalTime.parse(endTimeField.getText().trim());
                String desc = descriptionArea.getText().trim();

                if (title.isEmpty() || start == null || end == null) {
                    throw new IllegalArgumentException("All fields must be filled");
                }

                createdEvent = new Event(title, start, end, desc);
                dispose(); // close dialog

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public Event getCreatedEvent() {
        return createdEvent;
    }
}

