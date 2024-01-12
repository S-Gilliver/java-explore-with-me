package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventSearchParams;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.AdminEventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
@Validated
public class EventControllerAdmin {
    private final AdminEventService adminEventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
                                           @PathVariable @PositiveOrZero int eventId) {
        log.info("PATCH event by admin");
        return adminEventService.patchEvent(updateEventAdminRequest, eventId);
    }

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(@RequestParam(required = false)EventSearchParams eventSearchParams,
                                               @RequestParam(defaultValue = "0")@PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET events by admin with params: ", eventSearchParams, from, size);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return adminEventService.getEvents(eventSearchParams, pageRequest);
    }
}
