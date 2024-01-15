package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NewEventDto {
    @NotEmpty
    @NotNull
    @Size(min = 20, message = "String too short")
    @Size(max = 2000, message = "String too long")
    private final String annotation;
    @Positive
    private final int category;
    @NotEmpty
    @NotNull
    @Size(min = 20, message = "String too short")
    @Size(max = 7000, message = "String too long")
    private final String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;
    @NotNull
    private final Location location;
    private Boolean paid = false;
    private int participantLimit = 0;
    private Boolean requestModeration = true;
    @NotEmpty
    @NotNull
    @Size(min = 3, message = "String too short")
    @Size(max = 120, message = "String too long")
    private final String title;
}