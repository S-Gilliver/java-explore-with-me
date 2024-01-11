package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {
    ParticipationRequestDto postRequest(int userId, int eventId);

    List<ParticipationRequestDto> getRequests(int userId);

    ParticipationRequestDto cancelRequest(int userId, int requestId);
}

