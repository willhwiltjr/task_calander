package ui;

import model.Event;

import java.time.LocalDateTime;

public interface CalendarActionListener {

    // Add a new event (e.g., from toolbar button)
    void onAdd();

    // Add a new event at a suggested date/time (e.g., from right-click)
    void onAdd(LocalDateTime suggestedTime);

    // Edit an existing event
    void onEdit(Event event);

    // Delete an existing event
    void onDelete(Event event);

    // An event was selected (click or focus)
    void onEventSelected(Event event);

    // Optional: For future navigation features
    void onDateSelected(LocalDateTime dateTime);
}


