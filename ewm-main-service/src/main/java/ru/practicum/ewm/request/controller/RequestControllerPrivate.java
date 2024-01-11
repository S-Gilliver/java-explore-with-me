package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.ewm.request.service.PrivateRequestService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestControllerPrivate {

    private final PrivateRequestService privateRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @PositiveOrZero int userId,
                                                 @RequestParam @PositiveOrZero int eventId) {
        log.info("POST request by userId {} for eventId {}", userId, eventId);
        return privateRequestService.postRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByOwner(@PathVariable @PositiveOrZero int userId,
                                                        @PathVariable @PositiveOrZero int requestId) {
        log.info("PATCH cancel request by requesterId {} for requestId {}", userId, requestId);
        return privateRequestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable @PositiveOrZero int userId) {
        log.info("GET all requests by requesterId {}", userId);
        return privateRequestService.getRequests(userId);
    }
}
