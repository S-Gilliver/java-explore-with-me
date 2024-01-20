package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.CommentDto;
import ru.practicum.ewm.event.dto.CommentRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    EventFullDto postEvent(NewEventDto newEventDto, int userId);

    List<EventShortDto> getUserEvents(int userId, PageRequest pageRequest);

    EventFullDto getEventById(int userId, int eventId);

    EventFullDto patchEvent(UpdateEventUserRequest updateEventUserRequest, int userId, int eventId);

    List<ParticipationRequestDto> getRequestsInEvent(int userId, int eventId);

    EventRequestStatusUpdateResult patchRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, int userId, int eventId);

    CommentDto postComment(CommentRequest commentRequest, int userId, int eventId);

    CommentDto patchComment(CommentRequest commentRequest, int userId, int eventId, int commentId);

    void deleteComment(int userId, int eventId, int commentId);

    List<CommentDto> getComments(int userId, int eventId, PageRequest pageRequest);
}
