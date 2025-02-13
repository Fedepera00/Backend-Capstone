package it.epicode.patronato_gestionale.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleCalendarEventRequest {
    private String summary;
    private String location;
    private String description;
    private String startDateTime;
    private String endDateTime;
}