package model;

import java.time.Duration;
import java.time.LocalTime;

public class Event {
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;

    //constructor
    public Event(String title, LocalTime start, LocalTime end, String notes) {
        this.title = title;
        this.startTime = start;
        this.endTime = end;
        this.description = notes;
    }

    //getters
    //get tile retrieves events title
    public String getTitle(){        return this.title;    }
    //get end time
    public LocalTime getEndTime() {return this.endTime; }
    //get start time
    public LocalTime getStartTime(){return this.startTime; }
    //get description
    public String getDescription(){ return this.description; }
    //get duration
    public Duration getDuration(){ return Duration.between(startTime, endTime); }
    //get duration long
    public long getDurationMinutes(){ return getDuration().toMinutes(); }

    //setters
    //set title
    public void setTitle(String title) { this.title = title;  }
    //set start
    public void setStartTime(LocalTime startTime) { this.startTime = startTime;  }
    //set end
    public void setEndTime(LocalTime endTime) { this.endTime = endTime;  }
    //set description
    public void setDescription(String description) { this.description = description;  }
}
