package model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Event {
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;

    //constructor
    public Event(String title, LocalDateTime start, LocalDateTime end, String notes) {
        this.title = title;
        this.startDateTime = start;
        this.endDateTime = end;
        this.description = notes;
    }

    //getters
    //get tile retrieves events title
    public String getTitle(){        return this.title;    }
    //get end time
    public LocalTime getEndTime() {return this.endDateTime.toLocalTime(); }
    //get start time
    public LocalTime getStartTime(){return this.startDateTime.toLocalTime(); }
    //get description
    public String getDescription(){ return this.description; }
    //get duration
    public Duration getDuration(){ return Duration.between(startDateTime, endDateTime); }
    //get duration long
    public long getDurationMinutes(){ return getDuration().toMinutes(); }
    //get startDateTime
    public LocalDateTime getStartDateTime() {  return this.startDateTime;  }
    //get endDateTime
    public LocalDateTime getEndDateTime() {  return this.endDateTime;  }
    //get dayOfWeek
    public DayOfWeek getDayOfWeek() {  return this.startDateTime.getDayOfWeek();  }

    //setters
    //set title
    public void setTitle(String title) { this.title = title;  }
    //set start
    public void setStartTime(LocalDateTime startTime) { this.startDateTime = startTime;  }
    //set end
    public void setEndTime(LocalDateTime endTime) { this.endDateTime = endTime;  }
    //set description
    public void setDescription(String description) { this.description = description;  }
}
