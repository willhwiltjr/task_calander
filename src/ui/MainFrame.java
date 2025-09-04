package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CalendarPanel calendarPanel;

    public MainFrame() {
        setTitle("Daily Scheduler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null); // center the window
        setLayout(new BorderLayout());

        // Sidebar with buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(200, 600));

        JButton addEventBtn = new JButton("Add Event");
        JButton dailyViewBtn = new JButton("Daily View");
        JButton monthlyViewBtn = new JButton("Monthly View");

        // Add buttons to sidebar
        buttonPanel.add(addEventBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(dailyViewBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(monthlyViewBtn);

        // Calendar display (hourly)
        calendarPanel = new CalendarPanel();

        // Add both to frame
        add(buttonPanel, BorderLayout.WEST);
        add(calendarPanel, BorderLayout.CENTER);

        setVisible(true);

        // === Future: Add ActionListeners ===
        // addEventBtn.addActionListener(e -> openAddEventDialog());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}


