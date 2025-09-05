package ui;

import model.Event;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;

public class MonthlyCalendarPanel extends JPanel {
    private final CalendarGridPanel calendarGridPanel;
    private final JLabel monthLabel;

    public MonthlyCalendarPanel(List<Event> events) {
        setLayout(new BorderLayout());

        // Header with month name
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(monthLabel, BorderLayout.NORTH);

        // Calendar grid
        calendarGridPanel = new CalendarGridPanel(events);
        add(calendarGridPanel, BorderLayout.CENTER);

        setMonth(YearMonth.now());
    }

    public void setEvents(List<Event> events) {
        calendarGridPanel.setEvents(events);
    }

    public void setMonth(YearMonth month) {
        calendarGridPanel.setMonth(month);
        monthLabel.setText(month.getMonth() + " " + month.getYear());
    }

    public void setCalendarActionListener(CalendarActionListener listener) {
        calendarGridPanel.setCalendarActionListener(listener);
    }
}



