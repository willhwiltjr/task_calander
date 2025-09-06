package ui;

import model.LocalEvent;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WeeklyCalendarPanel extends JPanel {
    private List<LocalEvent> localEvents;
    private CalendarActionListener listener;

    public void setCalendarActionListener(CalendarActionListener listener) {  this.listener = listener;  }

    public WeeklyCalendarPanel(List<LocalEvent> localEvents) {
        this.localEvents = localEvents;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600)); // Temporary size
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int dayColumnWidth = panelWidth / 7;
        int headerHeight = 30;
        int eventHeight = 50;
        int verticalPadding = 10;

        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        // Draw each day column
        for (int i = 0; i < 7; i++) {
            int x = i * dayColumnWidth;

            // Background stripe for day
            g2.setColor(new Color(245, 245, 245));
            g2.fillRect(x, 0, dayColumnWidth, panelHeight);

            // Day header
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString(dayNames[i], x + 10, 20);

            // Draw event blocks
            if (localEvents != null) {
                int yOffset = headerHeight + verticalPadding;
                for (LocalEvent localEvent : localEvents) {
                    if (localEvent.getDayOfWeek().getValue() % 7 == i) {
                        g2.setColor(new Color(100, 180, 255, 180));
                        g2.fillRoundRect(x + 10, yOffset, dayColumnWidth - 20, eventHeight, 10, 10);

                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                        g2.drawString(localEvent.getTitle(), x + 16, yOffset + 16);

                        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                        g2.drawString(localEvent.getStartDateTime().toLocalTime().toString(), x + 16, yOffset + 32);

                        yOffset += eventHeight + verticalPadding;
                    }
                }
            }

            // Draw vertical line
            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(x, 0, x, panelHeight);
        }
    }


    public void setEvents(List<LocalEvent> localEvents) {
        this.localEvents = localEvents;
        repaint();
    }
}

