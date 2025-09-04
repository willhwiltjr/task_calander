package ui;

import model.Event;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CalendarPanel extends JPanel {
    private List<Event> events;

    public CalendarPanel() {
        this.events = new ArrayList<>();
        setBackground(Color.WHITE);
    }

    public void addEvent(Event event) {
        events.add(event);
        repaint();  // Triggers paintComponent to redraw events
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw time grid, then events
        drawTimeGrid(g);
        drawEvents(g);
    }

    private void drawTimeGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        int hourHeight = 40;  // 40 pixels per hour
        for (int i = 0; i < 24; i++) {
            int y = i * hourHeight;
            g.drawLine(0, y, getWidth(), y);
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.format("%02d:00", i), 5, y + 12);
            g.setColor(Color.LIGHT_GRAY);
        }
    }


    private void drawEvents(Graphics g) {
        int hourHeight = 40;
        int x = 100;            // starting x for event box
        int width = 300;        // fixed width for events

        g.setColor(new Color(100, 150, 255, 180)); // semi-transparent blue

        for (Event event : events) {
            int startHour = event.getStartTime().getHour();
            int startMinute = event.getStartTime().getMinute();
            int endHour = event.getEndTime().getHour();
            int endMinute = event.getEndTime().getMinute();

            int yStart = (int) ((startHour * 60 + startMinute) * (hourHeight / 60.0));
            int yEnd = (int) ((endHour * 60 + endMinute) * (hourHeight / 60.0));
            int height = yEnd - yStart;

            g.fillRoundRect(x, yStart, width, height, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(event.getTitle(), x + 10, yStart + 15);
            g.drawString(event.getStartTime() + " - " + event.getEndTime(), x + 10, yStart + 30);

            g.setColor(new Color(100, 150, 255, 180)); // reset color for next box
        }
    }

}

