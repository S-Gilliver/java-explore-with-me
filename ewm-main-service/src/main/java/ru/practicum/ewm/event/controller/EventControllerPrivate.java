package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}/events")
@AllArgsConstructor
public class EventControllerPrivate {

    public final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEvent) {
        log.info("POST event for private");
        return eventService.createEvent(userId, newEvent);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwnerId(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @Valid @RequestBody UpdateEventUserRequest userRequest) {

        log.info("PATCH request for private /users/{}/events/{} received. Provided DTO: {}",
                userId, eventId, userRequest);
        return eventService.updateEventByOwnerId(userId, eventId, userRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequestByOwnerId(@PathVariable Long userId,
                                                                       @PathVariable Long eventId,
                                                                       @Valid @RequestBody EventRequestStatusUpdateRequest
                                                                               request) {
        log.info("PATCH all requests status by event owner for private");
        return eventService.updateStatusRequestByOwnerId(userId, eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getEventsByOwnerId(@PathVariable Long userId,
                                                  @Min(0) @RequestParam(defaultValue = "0") int from,
                                                  @Min(0) @RequestParam(defaultValue = "10") int size) {
        log.info("GET events by owner for private");
        return eventService.getEventsByOwnerId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByOwnerId(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        log.info("GET event by owner for private");
        return eventService.getEventByOwnerId(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerId(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {

        log.info("GET all requests for event owner for private");
        return eventService.getRequestsByOwnerId(userId, eventId);
    }
}
