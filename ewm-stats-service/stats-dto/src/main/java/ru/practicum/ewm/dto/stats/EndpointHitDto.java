package ru.practicum.ewm.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {

    private Long id;

    @NotBlank(message = "The app parameter must not be empty")
    private String app;

    @NotBlank(message = "The uri parameter must not be empty")
    private String uri;

    @NotBlank
    private String ip;

    @NotBlank
    private String timestamp;
}
