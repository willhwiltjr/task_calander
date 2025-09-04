package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;

import model.Event;

public class MainFrame extends JFrame implements CalendarActionListener {
    private JScrollPane scrollPane;  // <-- Add this at the class level
    private CalendarPanel calendarPanel;
    private DefaultListModel<Event> eventListModel;
    private JList<Event> eventList;
    private JPopupMenu eventContextMenu;



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
        JButton zoomInBtn = new JButton("Zoom In");
        JButton zoomOutBtn = new JButton("Zoom Out");

        // Add buttons to sidebar
        buttonPanel.add(addEventBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(dailyViewBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(monthlyViewBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(zoomInBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(zoomOutBtn);



        // Calendar display (hourly)
        calendarPanel = new CalendarPanel();
        scrollPane = new JScrollPane(calendarPanel);
        calendarPanel.setCalendarActionListener(this);


        //======event list panel====
        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventList.setCellRenderer(new EventCellRenderer()); // optional, custom format
        eventList.setFixedCellHeight(30);
        eventList.setBorder(BorderFactory.createTitledBorder("Today's Events"));

        //===context menu creation===
        eventContextMenu = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem addItem = new JMenuItem("Add New Event");

        eventContextMenu.add(editItem);
        eventContextMenu.add(deleteItem);
        eventContextMenu.addSeparator();
        eventContextMenu.add(addItem);


        // Add listener to scroll on selection
        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Event selected = eventList.getSelectedValue();
                if (selected != null) {
                    scrollToEvent(selected);
                }
            }
        });

        eventList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = eventList.locationToIndex(e.getPoint());
                    if (row != -1) {
                        eventList.setSelectedIndex(row);
                        eventContextMenu.show(eventList, e.getX(), e.getY());
                    }
                }
            }
        });


        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(250, 600));
        rightPanel.add(new JScrollPane(eventList), BorderLayout.CENTER);

        // Add both to frame
        add(buttonPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);  // ← the new event list


        setVisible(true);
        scrollToCurrentHour();

        // === Future: Add ActionListeners ===
        addEventBtn.addActionListener(this::handleAddEvent);
        zoomInBtn.addActionListener(e -> {
            calendarPanel.setHourHeight(calendarPanel.getHourHeight() + 10);
            scrollToCurrentHour();
        });

        zoomOutBtn.addActionListener(e -> {
            calendarPanel.setHourHeight(Math.max(20, calendarPanel.getHourHeight() - 10));
            scrollToCurrentHour();
        });

        editItem.addActionListener(e -> {
            Event selected = eventList.getSelectedValue();
            if (selected != null) {
                AddEventDialog dialog = new AddEventDialog(this, selected);  // you’ll need to support this overload
                dialog.setVisible(true);
                Event updated = dialog.getCreatedEvent();
                if (updated != null) {
                    calendarPanel.updateEvent(selected, updated); // you'll implement this
                    eventListModel.set(eventList.getSelectedIndex(), updated);
                }
            }
        });

        deleteItem.addActionListener(e -> {
            Event selected = eventList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this event?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    calendarPanel.removeEvent(selected);
                    eventListModel.removeElement(selected);
                }
            }
        });

        addItem.addActionListener(this::handleAddEvent);  // reuse your existing add dialog

    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    private void handleAddEvent(ActionEvent e) {
        AddEventDialog dialog = new AddEventDialog(this);
        dialog.setVisible(true);

        Event newEvent = dialog.getCreatedEvent();
        if (newEvent != null) {
            calendarPanel.addEvent(newEvent);
            eventListModel.addElement(newEvent);   // Add to list panel
            scrollToEvent(newEvent);
        }

    }

    public void scrollToEvent(Event event) {
        int y = event.getStartTime().getHour() * calendarPanel.getHourHeight();
        animateScrollTo(y, 400);  // 400ms smooth scroll
    }

    private void scrollToCurrentHour() {
        int currentHour = LocalTime.now().getHour();
        int y = currentHour * calendarPanel.getHourHeight();
        animateScrollTo(y, 400); // 400ms animation
    }

    private void animateScrollTo(int targetY, int durationMs) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        int startY = verticalBar.getValue();
        int distance = targetY - startY;

        int frames = durationMs / 15;
        Timer timer = new Timer(15, null);
        final int[] currentFrame = {0};

        timer.addActionListener(e -> {
            currentFrame[0]++;
            float progress = (float) currentFrame[0] / frames;
            progress = Math.min(1.0f, progress);
            // Easing (cosine)
            progress = (float) (1 - Math.cos(progress * Math.PI)) / 2;

            int newY = startY + Math.round(distance * progress);
            verticalBar.setValue(newY);

            if (progress >= 1.0f) {
                timer.stop();
            }
        });

        timer.start();
    }


    @Override
    public void onAdd() {
        AddEventDialog dialog = new AddEventDialog(this);
        dialog.setVisible(true);
        Event newEvent = dialog.getCreatedEvent();
        if (newEvent != null) {
            calendarPanel.addEvent(newEvent);
            scrollToEvent(newEvent);
        }
    }

    @Override
    public void onEdit(Event event) {
        AddEventDialog dialog = new AddEventDialog(this);
        dialog.setVisible(true);
        Event newEvent = dialog.getCreatedEvent();
        if (newEvent != null) {
            calendarPanel.removeEvent(event);
            calendarPanel.addEvent(newEvent);
        }
        System.out.println("Edit requested for: " + event.getTitle());
    }

    @Override
    public void onDelete(Event event) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete event: " + event.getTitle() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            calendarPanel.removeEvent(event); // implement this method to remove event & repaint
        }
    }

    @Override
    public void onEventSelected(Event event) {
        if (event != null) {
            System.out.println("Event selected: " + event.getTitle());
            // Maybe show event details in a panel, enable edit/delete buttons, etc.
        } else {
            System.out.println("No event selected");
            // Clear selection UI
        }
    }

}


