package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NewUserRequest {
    @NotBlank
    @Email
    @Size(min = 6, message = "String too short")
    @Size(max = 254, message = "String too long")
    private final String email;
    @NotBlank
    @Size(min = 2, message = "String too short")
    @Size(max = 250, message = "String too long")
    private final String name;
}
