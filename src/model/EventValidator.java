package model;

import java.time.LocalDateTime;

public class EventValidator {

    /**
     * Validates the basic structure of an event.
     *
     * @param title The event title
     * @param start Start date-time
     * @param end   End date-time
     * @return null if valid, otherwise an error message
     */
    public static String validate(String title, LocalDateTime start, LocalDateTime end) {
        if (title == null || title.trim().isEmpty()) {
            return "Event title cannot be empty.";
        }

        if (start == null || end == null) {
            return "Start and end times must be provided.";
        }

        if (end.isBefore(start) || end.equals(start)) {
            return "End time must be after start time.";
        }

        return null; // Valid
    }

    // Optional: You could later add overloaded methods to validate full Event objects
    public static String validate(Event event) {
        return validate(event.getTitle(), event.getStartDateTime(), event.getEndDateTime());
    }
}

