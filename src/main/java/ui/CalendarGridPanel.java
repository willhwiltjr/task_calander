package ui;


import model.LocalEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class CalendarGridPanel extends JPanel {
    private List<LocalEvent> localEvents;
    private YearMonth currentMonth;
    private CalendarActionListener listener;

    public CalendarGridPanel(List<LocalEvent> localEvents) {
        this.localEvents = localEvents;
        this.currentMonth = YearMonth.now();
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    public void setCalendarActionListener(CalendarActionListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<LocalEvent> localEvents) {
        this.localEvents = localEvents;
        repaint();
    }

    public void setMonth(YearMonth month) {
        this.currentMonth = month;
        repaint();
    }

    private void handleMouseClick(MouseEvent e) {
        int cellWidth = getWidth() / 7;
        int cellHeight = getHeight() / getRowCount();

        int col = e.getX() / cellWidth;
        int row = (e.getY() - 20) / cellHeight;

        int startDayOfWeek = currentMonth.atDay(1).getDayOfWeek().getValue() % 7;
        int dayIndex = row * 7 + col - startDayOfWeek + 1;

        if (dayIndex < 1 || dayIndex > currentMonth.lengthOfMonth()) return;

        LocalDate clickedDate = currentMonth.atDay(dayIndex);
        LocalDateTime clickedDateTime = clickedDate.atTime(9, 0);  // default 9am

        LocalEvent clickedEvent = getEventAt(e.getPoint(), clickedDate);

        if (SwingUtilities.isRightMouseButton(e)) {
            showContextMenu(e.getX(), e.getY(), clickedDateTime, clickedEvent);
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (clickedEvent != null && listener != null) {
                listener.onEventSelected(clickedEvent);
            } else if (listener != null) {
                listener.onDateSelected(clickedDateTime);
            }
        }
    }

    private void showContextMenu(int x, int y, LocalDateTime dateTime, LocalEvent localEvent) {
        JPopupMenu menu = new JPopupMenu();

        if (localEvent != null) {
            JMenuItem editItem = new JMenuItem("Edit");
            editItem.addActionListener(e -> listener.onEdit(localEvent));

            JMenuItem deleteItem = new JMenuItem("Delete");
            deleteItem.addActionListener(e -> listener.onDelete(localEvent));

            menu.add(editItem);
            menu.add(deleteItem);
        } else {
            JMenuItem addItem = new JMenuItem("Add Event");
            addItem.addActionListener(e -> listener.onAdd(dateTime));
            menu.add(addItem);
        }

        menu.show(this, x, y);
    }

    private LocalEvent getEventAt(Point point, LocalDate date) {
        // You could improve this by calculating exact cell & event bounds.
        return localEvents.stream()
                .filter(e -> e.getStartDateTime().toLocalDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    private int getRowCount() {
        int startDayOfWeek = currentMonth.atDay(1).getDayOfWeek().getValue() % 7;
        int days = currentMonth.lengthOfMonth();
        return (int) Math.ceil((days + startDayOfWeek) / 7.0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate firstDay = currentMonth.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Sunday = 0

        int rows = getRowCount();
        int cellWidth = width / 7;
        int cellHeight = height / rows;

        Font headerFont = new Font("SansSerif", Font.BOLD, 14);
        Font eventFont = new Font("SansSerif", Font.PLAIN, 12);

        g2.setFont(headerFont);
        String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            g2.drawString(dayHeaders[i], i * cellWidth + 5, 15);
        }

        g2.setFont(eventFont);
        int dayCounter = 1;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 7; col++) {
                int cellX = col * cellWidth;
                int cellY = row * cellHeight + 20; // offset for header row

                int index = row * 7 + col;
                if (index >= startDayOfWeek && dayCounter <= daysInMonth) {
                    LocalDate date = currentMonth.atDay(dayCounter);

                    // Draw border
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawRect(cellX, cellY, cellWidth, cellHeight);

                    // Draw day number
                    g2.setColor(Color.BLACK);
                    g2.drawString(Integer.toString(dayCounter), cellX + 5, cellY + 15);

                    // Draw events for the day
                    List<LocalEvent> dayEvents = localEvents.stream()
                            .filter(e -> e.getStartDateTime().toLocalDate().equals(date))
                            .toList();

                    int eventYOffset = 30;
                    for (LocalEvent e : dayEvents) {
                        g2.setColor(new Color(135, 206, 250));
                        g2.fillRoundRect(cellX + 5, cellY + eventYOffset - 10, cellWidth - 10, 18, 5, 5);

                        g2.setColor(Color.BLACK);
                        g2.drawString(e.getTitle(), cellX + 10, cellY + eventYOffset);
                        eventYOffset += 20;

                        if (eventYOffset > cellHeight - 10) break; // prevent overflow
                    }

                    dayCounter++;
                }
            }
        }
    }

}


