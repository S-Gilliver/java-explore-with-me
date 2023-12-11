package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.SearchFilterAdmin;
import ru.practicum.ewm.event.dto.SearchFilterPublic;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto newEvent);

    EventFullDto updateEventByOwnerId(Long userId, Long eventId, UpdateEventUserRequest userRequest);

    EventRequestStatusUpdateResult updateStatusRequestByOwnerId(Long userId,
                                                                Long eventId, EventRequestStatusUpdateRequest statusRequest);

    List<EventShortDto> getEventsByOwnerId(Long userId, PageRequest page);

    EventFullDto getEventByOwnerId(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByOwnerId(Long userId, Long eventId);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest adminRequest);

    List<EventFullDto> getEventsByAdmin(SearchFilterAdmin filterAdmin, PageRequest page);

    List<EventShortDto> getEventsPublic(SearchFilterPublic filterPublic);

    EventFullDto getEventByIdPublic(Long id);

    Event getEventByIdForService(Long eventId);
}
