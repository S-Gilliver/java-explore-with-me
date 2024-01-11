package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
public class EventControllerPrivate {

    private final PrivateEventService privateEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                                    @PathVariable @PositiveOrZero int userId) {
        log.info("POST event for private");
        return privateEventService.postEvent(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwnerId(@RequestBody @Valid UpdateEventUserRequest updateEventUserRequest,
                                             @PathVariable @PositiveOrZero int userId,
                                             @PathVariable @PositiveOrZero int eventId) {

        log.info("PATCH request for private /users/{}/events/{} received. Provided DTO: {}",
                userId, eventId, updateEventUserRequest);
        return privateEventService.patchEvent(updateEventUserRequest, userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequestByOwnerId(@RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                                       @PathVariable @PositiveOrZero int userId,
                                                                       @PathVariable @PositiveOrZero int eventId) {
        log.info("PATCH all requests status by event owner for private");
        return privateEventService.patchRequests(eventRequestStatusUpdateRequest, userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getEventsByOwnerId(@PathVariable @PositiveOrZero int userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET events by owner for private");
        return privateEventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByOwnerId(@PathVariable @PositiveOrZero int userId,
                                          @PathVariable @PositiveOrZero int eventId) {
        log.info("GET event by owner for private");
        return privateEventService.getEventById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerId(@PathVariable @PositiveOrZero int userId,
                                                              @PathVariable @PositiveOrZero int eventId) {

        log.info("GET all requests for event owner for private");
        return privateEventService.getRequestsInEvent(userId, eventId);
    }
}
