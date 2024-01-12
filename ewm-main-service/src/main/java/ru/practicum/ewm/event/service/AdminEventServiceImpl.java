package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventSearchParams;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.ModeratorEventState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, int eventId) {

        Event eventFromDb = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (updateEventAdminRequest.getStateAction() != null) {
            ModeratorEventState action = ModeratorEventState.valueOf(updateEventAdminRequest.getStateAction());
            switch (action) {
                case PUBLISH_EVENT:

                    if (eventFromDb.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot publish the event because it's not in the right state: PENDING");
                    }

                    if (LocalDateTime.now().plusHours(1).isAfter(eventFromDb.getEventDate())) {
                        throw new ConflictException("Cannot publish the event because the event date should be at least an hour after publication");
                    }

                    eventFromDb.setState(EventState.PUBLISHED);
                    eventFromDb.setPublishedOn(LocalDateTime.now());

                    break;

                case REJECT_EVENT:

                    if (eventFromDb.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot reject the event because it's not in the right state: PENDING");
                    }

                    eventFromDb.setState(EventState.CANCELED);

                    break;

                default:
                    throw new IllegalArgumentException("Invalid action: " + action);
            }
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            if (LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)).isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("Cannot publish the event because the event date should be at least an hour after publication");
            }
            eventFromDb.setEventDate(LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            eventFromDb.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null && updateEventAdminRequest.getCategory() != eventFromDb.getCategory().getId()) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", updateEventAdminRequest.getCategory())));
            eventFromDb.setCategory(category);
        }

        if (updateEventAdminRequest.getDescription() != null) {
            eventFromDb.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            eventFromDb.setLocation(updateEventAdminRequest.getLocation());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            eventFromDb.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getTitle() != null) {
            eventFromDb.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            eventFromDb.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        return EventMapper.createEventFullDto(eventRepository.save(eventFromDb), requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED));

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<EventFullDto> getEvents(EventSearchParams eventSearchParams, PageRequest pageRequest) {

        List<EventState> eventStates = null;
        if (eventSearchParams.getRangeStart() == null) {
            eventSearchParams.setRangeStart(LocalDateTime.now());
            eventSearchParams.setRangeEnd(eventSearchParams.getRangeStart().plusYears(1000));
        }

        if (eventSearchParams.getRangeEnd().isBefore(eventSearchParams.getRangeStart())) {
            throw new BadRequestException("Invalid time parameters");
        }

        if (eventSearchParams.getUsers() != null && eventSearchParams.getUsers().isEmpty()) {
            eventSearchParams.setUsers(null);
        }

        if (eventSearchParams.getStates() != null && eventSearchParams.getStates().isEmpty()) {
            eventSearchParams.setStates(null);
        }

        if (eventSearchParams.getCategories() != null && eventSearchParams.getCategories().isEmpty()) {
            eventSearchParams.setCategories(null);
        }

        if (eventSearchParams.getStates() != null) {
            eventStates = new ArrayList<>();
            for (String state : eventSearchParams.getStates()) {
                if (!EnumUtils.isValidEnum(EventState.class, state)) {
                    throw new BadRequestException("Invalid state: " + state);
                }
                eventStates.add(EventState.valueOf(state));
            }
        }

        Page<Event> events = eventRepository.getByUserIdsStatesCategories(eventSearchParams.getUsers(),
                eventStates, eventSearchParams.getCategories(), eventSearchParams.getRangeStart(),
                eventSearchParams.getRangeEnd(), pageRequest);

        return events.getContent().stream()
                .map(event -> EventMapper.createEventFullDto(event,
                        requestRepository.countRequestByEventIdAndStatus(event.getId(),
                                RequestStatus.CONFIRMED)))
                .collect(Collectors.toList());
    }
}
