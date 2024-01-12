package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventSearchParams;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {
    EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, int eventId);

    List<EventFullDto> getEvents(EventSearchParams eventSearchParams, PageRequest pageRequest);
}