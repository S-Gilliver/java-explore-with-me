package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.request.enums.RequestStatus;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EventRequestStatusUpdateRequest {
    private final List<Integer> requestIds;
    private final RequestStatus status;
}
