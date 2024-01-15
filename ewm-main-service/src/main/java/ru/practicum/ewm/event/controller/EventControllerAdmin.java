package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.AdminEventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
@Validated
public class EventControllerAdmin {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final AdminEventService adminEventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
                                           @PathVariable @PositiveOrZero int eventId) {
        log.info("PATCH event by admin");
        return adminEventService.patchEvent(updateEventAdminRequest, eventId);
    }

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(@RequestParam(required = false) List<Integer> users,
                                               @RequestParam(required = false) List<String> states,
                                               @RequestParam(required = false) List<Integer> categories,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET events by admin with params {}, {}, {}, {}, {}, {}, {}", users, states, categories, rangeStart, rangeEnd, from, size);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return adminEventService.getEvents(users, states, categories, rangeStart, rangeEnd, pageRequest);
    }
}
