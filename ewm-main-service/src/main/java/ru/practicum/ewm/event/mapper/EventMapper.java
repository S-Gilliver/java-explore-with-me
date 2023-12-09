package ru.practicum.ewm.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.enums.State;
import ru.practicum.ewm.event.enums.StateAction;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event mapToNewEvent(NewEventDto newEvent, Category category, User user) {
        return Event.builder()
                .annotation(newEvent.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEvent.getDescription())
                .eventDate(LocalDateTime.parse(newEvent.getEventDate(), FORMAT))
                .initiator(user)
                .paid(newEvent.getPaid() != null && newEvent.getPaid())
                .participantLimit(newEvent.getParticipantLimit() == null ? 0L : newEvent.getParticipantLimit())
                .requestModeration(newEvent.getRequestModeration() == null || newEvent.getRequestModeration())
                .title(newEvent.getTitle())
                .build();
    }

    public static Event mapToEventUpdate(Event event, UpdateEventUserRequest userRequest) {
        event.setAnnotation(userRequest.getAnnotation() != null ? userRequest.getAnnotation() : event.getAnnotation());
        event.setDescription(userRequest.getDescription() != null ? userRequest.getDescription() : event.getDescription());
        event.setEventDate(userRequest.getEventDate() != null ? LocalDateTime.parse(userRequest.getEventDate(), FORMAT) : event.getEventDate());
        event.setPaid(userRequest.getPaid() != null ? userRequest.getPaid() : event.getPaid());
        event.setParticipantLimit(userRequest.getParticipantLimit() != null ? userRequest.getParticipantLimit() : event.getParticipantLimit());
        event.setRequestModeration(userRequest.getRequestModeration() != null ? userRequest.getRequestModeration() : event.getRequestModeration());
        event.setTitle(userRequest.getTitle() != null ? userRequest.getTitle() : event.getTitle());
        event.setViews(event.getViews());
        if (userRequest.getStateAction() == null) {
            return event;
        }
        if (userRequest.getStateAction().equals(String.valueOf(StateAction.CANCEL_REVIEW))) {
            event.setState(State.CANCELED);
        }
        if (userRequest.getStateAction().equals(String.valueOf(StateAction.SEND_TO_REVIEW))) {
            event.setState(State.PENDING);
        }
        if (userRequest.getStateAction().equals(String.valueOf(StateAction.PUBLISH_EVENT))) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        if (userRequest.getStateAction().equals(String.valueOf(StateAction.REJECT_EVENT))) {
            event.setState(State.CANCELED);
        }
        return event;
    }

    public static EventFullDto mapToEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(FORMAT))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMAT))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .location(LocationMapper.mapToLocation(event.getLocationModel()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(FORMAT) : null)
                .requestModeration(event.getRequestModeration())
                .state(String.valueOf(event.getState()))
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static List<EventFullDto> mapToEventsFullDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());
    }

    public static EventShortDto matToEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(FORMAT))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static List<EventShortDto> mapToEventsShortDto(List<Event> events) {

        return events.stream()
                .map(EventMapper::matToEventShortDto)
                .collect(Collectors.toList());
    }
}