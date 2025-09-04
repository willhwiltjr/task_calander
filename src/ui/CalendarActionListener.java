package ui;

import model.Event;

import java.time.LocalTime;

public interface CalendarActionListener {
    void onAdd();
    void onEdit(Event event);
    void onDelete(Event event);
}

