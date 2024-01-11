package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.enums.StateAction;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UpdateEventUserRequest {
    @Size(min = 20, message = "String too short")
    @Size(max = 2000, message = "String too long")
    private final String annotation;
    private final Integer category;
    @Size(min = 20, message = "String too short")
    @Size(max = 7000, message = "String too long")
    private final String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;
    private final Location location;
    private final Boolean paid;
    @PositiveOrZero
    private final Integer participantLimit;
    private final Boolean requestModeration;
    private final StateAction stateAction;
    @Size(min = 3, message = "String too short")
    @Size(max = 120, message = "String too long")
    private final String title;
}
