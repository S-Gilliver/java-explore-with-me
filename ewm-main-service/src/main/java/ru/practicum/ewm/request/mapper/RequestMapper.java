package ru.practicum.ewm.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Request createRequest(User requester, Event event) {
        Request request = Request.builder()
                .requester(requester)
                .created(LocalDateTime.now())
                .event(event)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return request;
    }

    public static ParticipationRequestDto createParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public static EventRequestStatusUpdateResult createEventRequestStatusUpdateResult(List<Request> requests) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requests.stream().filter(request -> request.getStatus() == RequestStatus.CONFIRMED).map(RequestMapper::createParticipationRequestDto).collect(Collectors.toList()))
                .rejectedRequests(requests.stream().filter(request -> request.getStatus() == RequestStatus.REJECTED).map(RequestMapper::createParticipationRequestDto).collect(Collectors.toList()))
                .build();
    }
}

