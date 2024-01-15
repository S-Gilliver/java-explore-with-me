package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchParams {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private List<Integer> users;
    private List<String> states;
    private List<Integer> categories;
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime rangeEnd;
}