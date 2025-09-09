package ui;

import model.LocalEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CalendarPanel extends JPanel {
    private LocalDate currentDate = LocalDate.now();
    private List<LocalEvent> localEvents;
    private int HOUR_HEIGHT = 60;  // taller = more space per hour
    private static final int LEFT_MARGIN = 60;  // for time labels
    private static final int EVENT_WIDTH = 200;
    private CalendarActionListener eventListener;
    private JPopupMenu contextMenu;
    private LocalEvent clickedEvent = null;
    private LocalEvent selectedLocalEvent;

    public void setCalendarActionListener(CalendarActionListener listener) {
        this.eventListener = listener;
    }
    public void setCurrentDate(LocalDate date){
        this.currentDate = date;
        repaint();
    }
    private JPopupMenu createContextMenu(LocalEvent localEvent) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem addItem = new JMenuItem("Add Event");
        addItem.addActionListener(e -> {
            if (eventListener != null) eventListener.onAdd();
        });
        menu.add(addItem);

        if (localEvent != null) {
            JMenuItem editItem = new JMenuItem("Edit Event");
            editItem.addActionListener(e -> {
                if (eventListener != null) eventListener.onEdit(localEvent);
            });
            menu.add(editItem);

            JMenuItem deleteItem = new JMenuItem("Delete Event");
            deleteItem.addActionListener(e -> {
                if (eventListener != null) eventListener.onDelete(localEvent);
            });
            menu.add(deleteItem);
        }

        return menu;
    }
    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            LocalEvent clickedEvent = getEventAt(e.getPoint()); // You'll need to implement this
            JPopupMenu menu = createContextMenu(clickedEvent);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public CalendarPanel() {
        this.localEvents = new ArrayList<>();
        setBackground(new Color(250, 250, 250)); //off-white
        setPreferredSize(new Dimension(800, 24 * HOUR_HEIGHT));
        contextMenu = new JPopupMenu();

        JMenuItem addEventItem = new JMenuItem("Add Event");
        JMenuItem editEventItem = new JMenuItem("Edit Event");
        JMenuItem deleteEventItem = new JMenuItem("Delete Event");

        contextMenu.add(addEventItem);
        contextMenu.add(editEventItem);
        contextMenu.add(deleteEventItem);

// Add mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    LocalEvent localEvent = getEventAt(e.getPoint());
                    selectedLocalEvent = localEvent;          // <-- This line is missing in your code
                    repaint();                      // <-- Trigger repaint to show highlight

                    if (eventListener != null) {
                        eventListener.onEventSelected(localEvent);
                    }
                }
                maybeShowPopup(e); // for right-click popup
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
        });


    }

    public void addEvent(LocalEvent localEvent) {
        localEvents.add(localEvent);
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

        // Step 1: Sort events by start time
        List<LocalEvent> filteredEvents = localEvents.stream()
                .filter(e -> e.getStartDateTime().toLocalDate().equals(currentDate))
                .sorted(Comparator.comparing(LocalEvent::getStartTime))
                .toList();

        List<List<LocalEvent>> overlappingGroups = groupOverlappingEvents(localEvents);

        for (List<LocalEvent> group : overlappingGroups) {
            int groupSize = group.size();

            for (int i = 0; i < groupSize; i++) {
                LocalEvent localEvent = group.get(i);

                int startY = (int) (localEvent.getStartTime().toSecondOfDay() / 60.0 * HOUR_HEIGHT / 60);
                int endY = (int) (localEvent.getEndTime().toSecondOfDay() / 60.0 * HOUR_HEIGHT / 60);
                int height = endY - startY;

                int columnWidth = EVENT_WIDTH / groupSize;
                int x = LEFT_MARGIN + 10 + i * columnWidth;

                // Draw background
                g2.setColor(new Color(100, 180, 255, 180));
                g2.fillRoundRect(x, startY + 2, columnWidth - 5, height - 4, 12, 12);

                // Border
                g2.setColor(new Color(70, 130, 200));
                g2.drawRoundRect(x, startY + 2, columnWidth - 5, height - 4, 12, 12);

                // Text
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(localEvent.getTitle(), x + 6, startY + 18);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString(localEvent.getStartTime() + " - " + localEvent.getEndTime(), x + 6, startY + 34);

                if (!localEvent.getDescription().isEmpty()) {
                    g2.drawString(localEvent.getDescription(), x + 6, startY + 48);
                }

                if (localEvent.equals(selectedLocalEvent)) {
                    g2.setColor(new Color(255, 165, 0, 180)); // orange-ish highlight
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(x, startY + 2, columnWidth - 5, height - 4, 12, 12);
                    g2.setStroke(new BasicStroke(1)); // reset stroke
                }
            }
        }
    }

    private List<List<LocalEvent>> groupOverlappingEvents(List<LocalEvent> localEvents) {
        List<List<LocalEvent>> groups = new ArrayList<>();

        for (LocalEvent current : localEvents) {
            boolean added = false;

            for (List<LocalEvent> group : groups) {
                if (group.stream().anyMatch(e -> eventsOverlap(e, current))) {
                    group.add(current);
                    added = true;
                    break;
                }
            }

            if (!added) {
                List<LocalEvent> newGroup = new ArrayList<>();
                newGroup.add(current);
                groups.add(newGroup);
            }
        }

        return groups;
    }

    private boolean eventsOverlap(LocalEvent e1, LocalEvent e2) {
        return !e1.getEndTime().isBefore(e2.getStartTime()) &&
                !e1.getStartTime().isAfter(e2.getEndTime());
    }

    public int getHourHeight() { return HOUR_HEIGHT; }

    public void setHourHeight(int newHeight) {
        this.HOUR_HEIGHT = newHeight;
        setPreferredSize(new Dimension(800, 24 * HOUR_HEIGHT));
        revalidate(); // Update scrollpane layout
        repaint();    // Redraw with new height
    }

    public void updateEvent(LocalEvent oldLocalEvent, LocalEvent newLocalEvent) {
        int index = localEvents.indexOf(oldLocalEvent);
        if (index != -1) {
            localEvents.set(index, newLocalEvent);
            repaint();
        }
    }

    public void removeEvent(LocalEvent localEvent) {
        localEvents.remove(localEvent);
        repaint();
    }


    private LocalEvent getEventAt(Point point) {
        List<List<LocalEvent>> overlappingGroups = groupOverlappingEvents(localEvents);

        for (List<LocalEvent> group : overlappingGroups) {
            int groupSize = group.size();

            for (int i = 0; i < groupSize; i++) {
                LocalEvent localEvent = group.get(i);

                int startY = (int) (localEvent.getStartTime().toSecondOfDay() / 60.0 * HOUR_HEIGHT / 60);
                int endY = (int) (localEvent.getEndTime().toSecondOfDay() / 60.0 * HOUR_HEIGHT / 60);
                int height = endY - startY;

                int columnWidth = EVENT_WIDTH / groupSize;
                int x = LEFT_MARGIN + 10 + i * columnWidth;

                Rectangle eventRect = new Rectangle(x, startY + 2, columnWidth - 5, height - 4);
                if (eventRect.contains(point)) {
                    return localEvent;
                }
            }
        }
        return null;
    }

    public List<LocalEvent> getEvents() {  return this.localEvents;  }







}

