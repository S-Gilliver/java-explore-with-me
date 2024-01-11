package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CategoryDto {
    private final int id;
    @NotBlank
    @Size(min = 1, message = "String too short")
    @Size(max = 50, message = "String too long")
    private final String name;
}
