package ui;

import model.Event;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CalendarPanel extends JPanel {
    private List<Event> events;
    private static final int HOUR_HEIGHT = 60;  // taller = more space per hour
    private static final int LEFT_MARGIN = 60;  // for time labels
    private static final int EVENT_WIDTH = 200;


    public CalendarPanel() {
        this.events = new ArrayList<>();
        setBackground(new Color(250, 250, 250));  // off-white

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
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(230, 230, 230));  // light gray lines
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));

        for (int hour = 0; hour < 24; hour++) {
            int y = hour * HOUR_HEIGHT;
            if (hour % 2 == 0) {
                g.setColor(new Color(245, 245, 245));  // very light gray
                g.fillRect(LEFT_MARGIN, y, getWidth() - LEFT_MARGIN, HOUR_HEIGHT);
            }

            // Draw background line
            g2.drawLine(LEFT_MARGIN, y, getWidth(), y);

            // Draw time label
            String label = String.format("%02d:00", hour);
            g2.setColor(Color.GRAY);
            g2.drawString(label, 10, y + 15);
            g2.setColor(new Color(230, 230, 230));  // reset color for next line
        }
    }



    private void drawEvents(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Event event : events) {
            int startY = (int) (event.getStartTime().toSecondOfDay() / 60.0 * HOUR_HEIGHT / 60);
            int endY = (int) (event.getEndTime().toSecondOfDay() / 60.0 * HOUR_HEIGHT / 60);
            int height = endY - startY;

            int x = LEFT_MARGIN + 10;

            // Background box
            g2.setColor(new Color(100, 180, 255, 180));  // light blue with transparency
            g2.fillRoundRect(x, startY + 2, EVENT_WIDTH, height - 4, 12, 12);

            // Border
            g2.setColor(new Color(70, 130, 200));
            g2.drawRoundRect(x, startY + 2, EVENT_WIDTH, height - 4, 12, 12);

            // Text inside
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString(event.getTitle(), x + 10, startY + 18);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString(event.getStartTime() + " - " + event.getEndTime(), x + 10, startY + 35);

            if (!event.getDescription().isEmpty()) {
                g2.drawString(event.getDescription(), x + 10, startY + 50);
            }
        }
    }


}

