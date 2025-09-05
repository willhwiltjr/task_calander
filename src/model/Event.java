package model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private boolean allDay;
    private boolean recurring;
    private String location;
    private List<String> attendees;

    //no arg constructor for serialization frameworks
    public Event() {
        this.id = UUID.randomUUID();
    }
    //constructor
    public Event(String title, LocalDateTime start, LocalDateTime end, String notes) {
        this.title = title;
        this.startDateTime = start;
        this.endDateTime = end;
        this.description = notes;
    }

    //getters
    //uuid getter
    public UUID getId() { return id; }
    //get tile retrieves events title
    public String getTitle(){ return this.title; }
    //get end time
    public LocalTime getEndTime() { return this.endDateTime.toLocalTime(); }
    //get start time
    public LocalTime getStartTime(){ return this.startDateTime.toLocalTime(); }
    //get description
    public String getDescription(){ return this.description; }
    //get duration
    public Duration getDuration(){ return Duration.between(startDateTime, endDateTime); }
    //get duration long
    public long getDurationMinutes(){ return getDuration().toMinutes(); }
    //get startDateTime
    public LocalDateTime getStartDateTime() { return this.startDateTime; }
    //get endDateTime
    public LocalDateTime getEndDateTime() { return this.endDateTime; }
    //get dayOfWeek
    public DayOfWeek getDayOfWeek() { return this.startDateTime.getDayOfWeek(); }

    //setters
    //set title
    public void setTitle(String title) { this.title = title; }
    //set start
    public void setStartTime(LocalDateTime startTime) { this.startDateTime = startTime; }
    //set end
    public void setEndTime(LocalDateTime endTime) { this.endDateTime = endTime; }
    //set description
    public void setDescription(String description) { this.description = description; }
    //set uuid
    public void setId(UUID id) { this.id = id; }

    public boolean isValid() {
        return startDateTime != null &&
                endDateTime != null &&
                startDateTime.isBefore(endDateTime);
    }

    @Override
    public String toString() {
        return title + " (" + startDateTime.toLocalTime() + " - " + endDateTime.toLocalTime() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event other = (Event) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}
