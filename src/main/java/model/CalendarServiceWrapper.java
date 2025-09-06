package model;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarServiceWrapper {
    private final Calendar calendarService;
    private static final String CALENDAR_ID = "primary";

    public CalendarServiceWrapper() throws Exception {
        this.calendarService = CalendarService.initCalendarService();
    }

    public List<LocalEvent> fetchUpcomingEvents(int maxResults) throws IOException {
        Events googleEvents = calendarService.events()
                .list(CALENDAR_ID)
                .setMaxResults(maxResults)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return googleEvents.getItems().stream()
                .map(GoogleEventMapper::fromGoogleJson)
                .collect(Collectors.toList());
    }

    public LocalEvent createEvent(LocalEvent localEvent) throws IOException {
        Event gEvent = GoogleEventMapper.toGoogleJson(localEvent);
        Event inserted = calendarService.events()
                .insert(CALENDAR_ID, gEvent)
                .execute();

        return GoogleEventMapper.fromGoogleJson(inserted);
    }

    public LocalEvent updateEvent(String googleEventId, LocalEvent updatedLocalEvent) throws IOException {
        Event gEvent = GoogleEventMapper.toGoogleJson(updatedLocalEvent);
        Event result = calendarService.events()
                .update(CALENDAR_ID, googleEventId, gEvent)
                .execute();

        return GoogleEventMapper.fromGoogleJson(result);
    }

    public void deleteEvent(String googleEventId) throws IOException {
        calendarService.events()
                .delete(CALENDAR_ID, googleEventId)
                .execute();
    }
}


