package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
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
