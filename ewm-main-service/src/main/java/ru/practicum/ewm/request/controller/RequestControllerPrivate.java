package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.ParticipationRequestService;

import java.util.List;

@Validated
@RestController
@Slf4j
@RequestMapping(path = "/users/{userId}/requests")
@AllArgsConstructor
public class RequestControllerPrivate {

    public final ParticipationRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        log.info("POST request by userId {} for eventId {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByOwner(@PathVariable Long userId,
                                                        @PathVariable Long requestId) {
        log.info("PATCH cancel request by requesterId {} for requestId {}", userId, requestId);
        return requestService.cancelRequestByOwner(requestId, userId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable Long userId) {
        log.info("GET all requests by requesterId {}", userId);
        return requestService.getRequestsByUserId(userId);
    }
}
