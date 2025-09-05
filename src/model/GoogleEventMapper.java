package model;

import org.json.JSONObject;
import org.json.JSONArray;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GoogleEventMapper {

    private static final DateTimeFormatter GOOGLE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Converts a local Event to a JSONObject formatted for Google Calendar API
     */
    public static JSONObject toGoogleJson(Event event) {
        JSONObject json = new JSONObject();

        json.put("summary", event.getTitle());
        json.put("description", event.getDescription());

        // Google Calendar requires nested "start" and "end" objects
        JSONObject start = new JSONObject();
        start.put("dateTime", event.getStartDateTime().format(GOOGLE_DATE_TIME_FORMATTER));
        start.put("timeZone", "UTC"); // or use system time zone

        JSONObject end = new JSONObject();
        end.put("dateTime", event.getEndDateTime().format(GOOGLE_DATE_TIME_FORMATTER));
        end.put("timeZone", "UTC");

        json.put("start", start);
        json.put("end", end);

        return json;
    }

    /**
     * Converts a Google Calendar API event JSONObject to a local Event
     */
    public static Event fromGoogleJson(JSONObject googleEvent) {
        String title = googleEvent.optString("summary", "Untitled");
        String description = googleEvent.optString("description", "");

        JSONObject start = googleEvent.getJSONObject("start");
        JSONObject end = googleEvent.getJSONObject("end");

        LocalDateTime startDateTime = LocalDateTime.parse(
                start.getString("dateTime").substring(0, 19), GOOGLE_DATE_TIME_FORMATTER);

        LocalDateTime endDateTime = LocalDateTime.parse(
                end.getString("dateTime").substring(0, 19), GOOGLE_DATE_TIME_FORMATTER);

        return new Event(title, startDateTime, endDateTime, description);
    }
}


