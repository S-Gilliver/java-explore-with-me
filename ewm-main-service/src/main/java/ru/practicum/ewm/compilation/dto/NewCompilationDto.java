package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NewCompilationDto {

    private final List<Integer> events;
    private final Boolean pinned;
    @NotBlank
    @Size(min = 1, message =  "String too short")
    @Size(max = 50, message = "String too long")
    private final String title;
}
