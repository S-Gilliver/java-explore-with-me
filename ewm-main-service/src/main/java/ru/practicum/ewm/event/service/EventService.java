package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto newEvent);

    EventFullDto updateEventByOwnerId(Long userId, Long eventId, UpdateEventUserRequest userRequest);

    EventRequestStatusUpdateResult updateStatusRequestByOwnerId(Long userId,
                                                                Long eventId, EventRequestStatusUpdateRequest statusRequest);

    List<EventShortDto> getEventsByOwnerId(Long userId, int from, int size);

    EventFullDto getEventByOwnerId(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByOwnerId(Long userId, Long eventId);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest adminRequest);

    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<String> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    List<EventShortDto> getEventsPublic(String text,
                                        List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        int from,
                                        int size,
                                        HttpServletRequest request);

    EventFullDto getEventByIdPublic(Long id, HttpServletRequest request);

    Event getEventByIdForService(Long eventId);
}
