package ui;

import model.LocalEvent;

import java.time.LocalDateTime;

public interface CalendarActionListener {

    // Add a new event (e.g., from toolbar button)
    void onAdd();

    // Add a new event at a suggested date/time (e.g., from right-click)
    void onAdd(LocalDateTime suggestedTime);

    // Edit an existing event
    void onEdit(LocalEvent localEvent);

    // Delete an existing event
    void onDelete(LocalEvent localEvent);

    // An event was selected (click or focus)
    void onEventSelected(LocalEvent localEvent);

    // Optional: For future navigation features
    void onDateSelected(LocalDateTime dateTime);
}


