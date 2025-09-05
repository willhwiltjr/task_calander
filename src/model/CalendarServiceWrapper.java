package model;

import com.google.api.services.calendar.model.Event as GoogleCalEvent;
import com.google.api.services.calendar.model.Events as GoogleEvents;


import java.io.IOException;
import java.util.Calendar;

public class CalendarServiceWrapper {
    private final Calendar calendarService;
    private static final String CALENDAR_ID = "primary"; // You may want to make this configurable

    public CalendarServiceWrapper() throws Exception {
        this.calendarService = CalendarService.initCalendarService();
    }

    public List<Event> fetchUpcomingEvents(int maxResults) throws IOException {
        GoogleEvents googleEvents = calendarService.events()
                .list(CALENDAR_ID)
                .setMaxResults(maxResults)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return googleEvents.getItems().stream()
                .map(GoogleEventMapper::fromGoogleJson)
                .collect(Collectors.toList());
    }

    public Event createEvent(Event localEvent) throws IOException {
        GoogleCalEvent gEvent = GoogleEventMapper.toGoogleJson(localEvent);
        GoogleCalEvent inserted = calendarService.events()
                .insert(CALENDAR_ID, gEvent)
                .execute();

        return GoogleEventMapper.fromGoogleJson(inserted);
    }

    public Event updateEvent(String googleEventId, Event updatedLocalEvent) throws IOException {
        GoogleCalEvent gEvent = GoogleEventMapper.toGoogleJson(updatedLocalEvent);
        GoogleCalEvent result = calendarService.events()
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

