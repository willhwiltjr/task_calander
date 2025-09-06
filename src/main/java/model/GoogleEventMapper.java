package model;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class GoogleEventMapper {

    // Converts LocalEvent → Google Event
    public static Event toGoogleJson(LocalEvent event) {
        Event googleEvent = new Event();

        googleEvent.setSummary(event.getTitle());
        googleEvent.setDescription(event.getDescription());

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(
                        event.getStartDateTime().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .setTimeZone(TimeZone.getDefault().getID());

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(
                        event.getEndDateTime().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .setTimeZone(TimeZone.getDefault().getID());

        googleEvent.setStart(start);
        googleEvent.setEnd(end);

        return googleEvent;
    }

    // Converts Google Event → LocalEvent
    public static LocalEvent fromGoogleJson(Event gEvent) {
        String title = gEvent.getSummary();
        String description = gEvent.getDescription();

        DateTime startDateTime = gEvent.getStart().getDateTime();
        DateTime endDateTime = gEvent.getEnd().getDateTime();

        LocalDateTime start = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(startDateTime.getValue()), ZoneId.systemDefault());

        LocalDateTime end = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(endDateTime.getValue()), ZoneId.systemDefault());

        return new LocalEvent(title, start, end, description);
    }

}



