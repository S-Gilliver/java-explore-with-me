package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statClient;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {

        if (text.isBlank()) {
            text = "";
        }

        if (!sort.equalsIgnoreCase("EVENT_DATE") && !sort.equalsIgnoreCase("VIEWS")) {
            throw new BadRequestException("Invalid sorting argument. Can only be one of [EVENT_DATE, VIEWS] or empty");
        }

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = rangeStart.plusYears(1000);
        }

        if (rangeStart == null || rangeEnd == null || rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Invalid time parameters");
        }

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Page<Event> events;

        if (categories != null && categories.isEmpty()) {
            categories = null;
        }

        if (!paid) {
            if (onlyAvailable) {
                events = eventRepository.getBySearchAvailable(EventState.PUBLISHED, text, categories, rangeStart, rangeEnd, sort, page);
            } else {
                events = eventRepository.getBySearch(EventState.PUBLISHED, text, categories, rangeStart, rangeEnd, sort, page);
            }
        } else {
            if (onlyAvailable) {
                events = eventRepository.getBySearchAndPaidAvailable(EventState.PUBLISHED, text, categories, rangeStart, rangeEnd, true, sort, page);
            } else {
                events = eventRepository.getBySearchAndPaid(EventState.PUBLISHED, text, categories, rangeStart, rangeEnd, true, sort, page);
            }
        }

        return events.getContent().stream()
                .map(event -> EventMapper.createEventShortDto(event, requestRepository.countRequestByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public EventFullDto getEventById(int id) {

        Event eventFromDb = eventRepository.getByIdAndState(id, EventState.PUBLISHED).orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));

        List<String> uris = List.of("/events/" + id);
        ResponseEntity<Object> responseEntity = statClient.getStat(LocalDateTime.now().minusYears(1000), LocalDateTime.now().plusYears(1000), uris, true);
        int hits = 0;
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                ArrayList<Map> hitsMap = (ArrayList<Map>) responseEntity.getBody();
                hits = Integer.parseInt(String.valueOf(hitsMap.get(0).getOrDefault("hits", "0")));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
        eventFromDb.setViews(hits);
        return EventMapper.createEventFullDto(eventFromDb, requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED));

    }
}

