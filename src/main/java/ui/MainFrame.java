package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import model.LocalEvent;
import model.ViewMode;

public class MainFrame extends JFrame implements CalendarActionListener {
    private JScrollPane scrollPane;  // <-- Add this at the class level
    private CalendarPanel calendarPanel;
    private WeeklyCalendarPanel weeklyCalendarPanel;
    private MonthlyCalendarPanel monthlyCalendarPanel;
    private DefaultListModel<LocalEvent> eventListModel;
    private JList<LocalEvent> eventList;
    private JPopupMenu eventContextMenu;
    private JButton dailyViewBtn;
    private JButton weeklyViewBtn;
    private JButton monthlyViewBtn;




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
        this.dailyViewBtn = new JButton("Daily View");
        dailyViewBtn.setVisible(false);
        this.weeklyViewBtn = new JButton("Weekly View");
        this.monthlyViewBtn = new JButton("Monthly View");
        JButton zoomInBtn = new JButton("Zoom In");
        JButton zoomOutBtn = new JButton("Zoom Out");


        // Add buttons to sidebar
        buttonPanel.add(addEventBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(dailyViewBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(weeklyViewBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(monthlyViewBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(zoomInBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(zoomOutBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));




        // Calendar display (hourly)
        calendarPanel = new CalendarPanel();
        scrollPane = new JScrollPane(calendarPanel);
        calendarPanel.setCalendarActionListener(this);

        // calander display weekly
        weeklyCalendarPanel = new WeeklyCalendarPanel(calendarPanel.getEvents());
        weeklyCalendarPanel.setCalendarActionListener(this);

        //calander display monthly
        monthlyCalendarPanel = new MonthlyCalendarPanel(calendarPanel.getEvents());
        monthlyCalendarPanel.setCalendarActionListener(this);


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
                LocalEvent selected = eventList.getSelectedValue();
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
            LocalEvent selected = eventList.getSelectedValue();
            if (selected != null) {
                AddEventDialog dialog = new AddEventDialog(this, selected);  // you’ll need to support this overload
                dialog.setVisible(true);
                LocalEvent updated = dialog.getCreatedEvent();
                if (updated != null) {
                    updateEventInAllViews(selected, updated);
                    eventListModel.set(eventList.getSelectedIndex(), updated);
                }
            }
        });

        dailyViewBtn.addActionListener(e -> {
            showDailyView();
            setViewButtonsVisible(ViewMode.DAILY);
        } );

        weeklyViewBtn.addActionListener(e ->{
            showWeeklyView();
            setViewButtonsVisible(ViewMode.WEEKLY);
        });

        monthlyViewBtn.addActionListener(e -> {
            showMonthlyView();
            setViewButtonsVisible(ViewMode.MONTHLY);
        });

        deleteItem.addActionListener(e -> {
            LocalEvent selected = eventList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this event?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    removeEventFromAllViews(selected);
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

        LocalEvent newLocalEvent = dialog.getCreatedEvent();
        if (newLocalEvent != null) {
            addEventToAllViews(newLocalEvent);
            eventListModel.addElement(newLocalEvent); // Add to list panel
            scrollToEvent(newLocalEvent);
        }

    }

    public void scrollToEvent(LocalEvent localEvent) {
        int y = localEvent.getStartTime().getHour() * calendarPanel.getHourHeight();
        animateScrollTo(y, 400);  // 400ms smooth scroll
    }

    public void scrollToHour(LocalDateTime dateTime) {
        int y = dateTime.getHour() * calendarPanel.getHourHeight();
        animateScrollTo(y, 400);  // 400ms smooth scroll animation
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

        LocalEvent newLocalEvent = dialog.getCreatedEvent();
        if (newLocalEvent != null) {
            addEventToAllViews(newLocalEvent);
            eventListModel.addElement(newLocalEvent);
            scrollToEvent(newLocalEvent);
        }
    }

    @Override
    public void onAdd(LocalDateTime suggestedTime) {
        AddEventDialog dialog = new AddEventDialog(this, suggestedTime);
        dialog.setVisible(true);

        LocalEvent newLocalEvent = dialog.getCreatedEvent();
        if (newLocalEvent != null) {
            addEventToAllViews(newLocalEvent);
            eventListModel.addElement(newLocalEvent);
            scrollToEvent(newLocalEvent);
        }
    }

    @Override
    public void onEdit(LocalEvent localEvent) {
        AddEventDialog dialog = new AddEventDialog(this, localEvent);
        dialog.setVisible(true);

        LocalEvent updated = dialog.getCreatedEvent();
        if (updated != null) {
            updateEventInAllViews(localEvent, updated);
            updateEventInList(localEvent, updated);
            scrollToEvent(updated);
        }
    }

    @Override
    public void onDelete(LocalEvent localEvent) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this event?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            removeEventFromAllViews(localEvent);
            eventListModel.removeElement(localEvent);
        }
    }

    @Override
    public void onEventSelected(LocalEvent localEvent) {
        if (localEvent != null) {
            System.out.println("Selected: " + localEvent.getTitle());
            eventList.setSelectedValue(localEvent, true);
            scrollToEvent(localEvent);
        }
    }

    @Override
    public void onDateSelected(LocalDateTime dateTime) {
        scrollToHour(dateTime);  // optionally use this
    }

    //switch view to daily view hook up to the daily view button
    private void showDailyView() {
        scrollPane.setViewportView(calendarPanel);
        calendarPanel.setCurrentDate(LocalDate.now());
    }

    //switch view to weekly view hook up to weekly view button
    private void showWeeklyView() {
        weeklyCalendarPanel.setEvents(calendarPanel.getEvents());
        scrollPane.setViewportView(weeklyCalendarPanel);
    }

    //switch view to monthly view hook up to monthly view button
    private void showMonthlyView() {
        monthlyCalendarPanel.setEvents(calendarPanel.getEvents());
        scrollPane.setViewportView(monthlyCalendarPanel);
    }

    private void addEventToAllViews(LocalEvent localEvent) {
        calendarPanel.addEvent(localEvent);
        weeklyCalendarPanel.setEvents(calendarPanel.getEvents());
        monthlyCalendarPanel.setEvents(calendarPanel.getEvents());
    }

    private void updateEventInAllViews(LocalEvent oldLocalEvent, LocalEvent newLocalEvent) {
        calendarPanel.removeEvent(oldLocalEvent);
        calendarPanel.addEvent(newLocalEvent);
        weeklyCalendarPanel.setEvents(calendarPanel.getEvents());
        monthlyCalendarPanel.setEvents(calendarPanel.getEvents());
    }

    private void updateEventInList(LocalEvent oldLocalEvent, LocalEvent newLocalEvent) {
        int index = eventListModel.indexOf(oldLocalEvent);
        if (index >= 0) {
            eventListModel.set(index, newLocalEvent);
        }
    }


    private void removeEventFromAllViews(LocalEvent localEvent) {
        calendarPanel.removeEvent(localEvent);
        weeklyCalendarPanel.setEvents(calendarPanel.getEvents());
        monthlyCalendarPanel.setEvents(calendarPanel.getEvents());
    }

    private void setViewButtonsVisible(ViewMode mode)
    {
        switch (mode) {
            case DAILY -> {
                dailyViewBtn.setVisible(false);
                weeklyViewBtn.setVisible(true);
                monthlyViewBtn.setVisible(true);
            }
            case WEEKLY -> {
                dailyViewBtn.setVisible(true);
                weeklyViewBtn.setVisible(false);
                monthlyViewBtn.setVisible(true);
            }
            case MONTHLY -> {
                dailyViewBtn.setVisible(true);
                weeklyViewBtn.setVisible(true);
                monthlyViewBtn.setVisible(false);
            }
        }
    }



}


