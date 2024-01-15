package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ParticipationRequestDto {
    private final int id;
    private final int event;
    private final String created;
    private final int requester;
    private final String status;
}
