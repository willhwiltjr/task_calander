package storage;

import model.LocalEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventStore {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    // Main method to save the event as .ics
    public static void saveEvent(LocalEvent event, String directoryPath) throws IOException {
        String content = buildEventContent(event);
        Path filePath = Path.of(directoryPath, event.getId().toString() + ".ics");
        Files.writeString(filePath, content);
    }

    // Build full ICS event content
    private static String buildEventContent(LocalEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\n");
        sb.append("VERSION:2.0\n");
        sb.append("PRODID:-//YourApp//Scheduler//EN\n");
        sb.append("BEGIN:VEVENT\n");

        appendLine(sb, "UID", event.getId().toString());
        appendLine(sb, "SUMMARY", escapeIcs(event.getTitle()));

        if (notEmpty(event.getDescription())) {
            appendLine(sb, "DESCRIPTION", escapeIcs(event.getDescription()));
        }

        if (notEmpty(event.getLocation())) {
            appendLine(sb, "LOCATION", escapeIcs(event.getLocation()));
        }

        if (event.getAttendees() != null) {
            appendAttendees(sb, event.getAttendees());
        }

        String start = formatter.format(event.getStartDateTime());
        String end = formatter.format(event.getEndDateTime());

        appendLine(sb, "DTSTAMP", start);
        appendLine(sb, "DTSTART", start);
        appendLine(sb, "DTEND", end);

        sb.append("END:VEVENT\n");
        sb.append("END:VCALENDAR\n");

        return sb.toString();
    }

    // Append a property line
    private static void appendLine(StringBuilder sb, String key, String value) {
        sb.append(key).append(":").append(value).append("\n");
    }

    // Append multiple attendees
    private static void appendAttendees(StringBuilder sb, List<String> attendees) {
        for (String attendee : attendees) {
            appendLine(sb, "ATTENDEE", "MAILTO:" + attendee);
        }
    }

    // Escape special characters for ICS format
    private static String escapeIcs(String input) {
        return input.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace(",", "\\,")
                .replace(";", "\\;");
    }

    // Check if string is not null or empty
    private static boolean notEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    //retrieval
    public static LocalEvent readEventFromFile(File icsFile) throws Exception {
        Map<String, List<String>> properties = readIcsProperties(icsFile);

        LocalEvent event = new LocalEvent();

        // Parse UUID
        List<String> uid = properties.get("UID");
        if (uid != null && !uid.isEmpty()) {
            event.setId(UUID.fromString(uid.get(0)));
        }

        // Parse title
        List<String> summary = properties.get("SUMMARY");
        if (summary != null && !summary.isEmpty()) {
            event.setTitle(unescapeIcs(summary.get(0)));
        }

        // Parse description
        List<String> description = properties.get("DESCRIPTION");
        if (description != null && !description.isEmpty()) {
            event.setDescription(unescapeIcs(description.get(0)));
        }

        // Parse location
        List<String> location = properties.get("LOCATION");
        if (location != null && !location.isEmpty()) {
            event.setLocation(unescapeIcs(location.get(0)));
        }

        // Parse attendees
        List<String> attendeeLines = properties.get("ATTENDEE");
        if (attendeeLines != null && !attendeeLines.isEmpty()) {
            List<String> attendees = new ArrayList<>();
            for (String line : attendeeLines) {
                if (line.startsWith("MAILTO:")) {
                    attendees.add(line.substring("MAILTO:".length()));
                }
            }
            event.setAttendees(attendees);
        }

        // Parse times


        List<String> startTimes = properties.get("DTSTART");
        if (startTimes == null || startTimes.isEmpty()) {
            System.err.println("Invalid event: missing DTSTART in file " + icsFile.getName());
            return null;
        }

        List<String> endTimes = properties.get("DTEND");

        if (startTimes != null && endTimes != null &&
                !startTimes.isEmpty() && !endTimes.isEmpty()) {

            LocalDateTime start = LocalDateTime.parse(startTimes.get(0), formatter);
            LocalDateTime end = LocalDateTime.parse(endTimes.get(0), formatter);

            event.setStartTime(start);
            event.setEndTime(end);
        }

        return event;
    }

    // Helper: Read key-value pairs from .ics
    private static Map<String, List<String>> readIcsProperties(File file) throws Exception {
        Map<String, List<String>> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                }
            }
        }

        return map;
    }

    // Reuse escape logic in reverse
    private static String unescapeIcs(String input) {
        return input.replace("\\n", "\n")
                .replace("\\,", ",")
                .replace("\\;", ";")
                .replace("\\\\", "\\");
    }

    //bulk loading of events
    public static ArrayList<LocalEvent> loadEventsFromDirectory(String directoryPath) {
        ArrayList<LocalEvent> events = new ArrayList<>();
        File dir = new File(directoryPath);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Invalid directory: " + directoryPath);
            return events;
        }

        File[] files = dir.listFiles((file, name) -> name.toLowerCase().endsWith(".ics"));

        if (files == null) {
            System.err.println("No .ics files found in directory: " + directoryPath);
            return events;
        }

        for (File file : files) {
            try {
                LocalEvent event = readEventFromFile(file);
                if (event != null) {
                    events.add(event);
                }
            } catch (Exception e) {
                System.err.println("Failed to read file: " + file.getName());
                e.printStackTrace();
            }
        }

        return events;
    }

}

