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
import java.util.Objects;

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
                .paid(Objects.requireNonNullElse(newEvent.getPaid(), false))
                .participantLimit(Objects.requireNonNullElse(newEvent.getParticipantLimit(), 0L))
                .requestModeration(Objects.requireNonNullElse(newEvent.getRequestModeration(), true))
                .title(newEvent.getTitle())
                .build();
    }

    public static Event mapToEventUpdate(Event event, UpdateEventUserRequest userRequest) {
        event.setAnnotation(Objects.requireNonNullElse(userRequest.getAnnotation(), event.getAnnotation()));
        event.setDescription(Objects.requireNonNullElse(userRequest.getDescription(), event.getDescription()));
        event.setEventDate((LocalDateTime) Objects.requireNonNullElse(userRequest.getEventDate(), event.getEventDate()));
        event.setPaid(Objects.requireNonNullElse(userRequest.getPaid(), event.getPaid()));
        event.setParticipantLimit(Objects.requireNonNullElse(userRequest.getParticipantLimit(), event.getParticipantLimit()));
        event.setRequestModeration(Objects.requireNonNullElse(userRequest.getRequestModeration(), event.getRequestModeration()));
        event.setTitle(Objects.requireNonNullElse(userRequest.getTitle(), event.getTitle()));
        event.setViews(event.getViews());

        if (userRequest.getStateAction() == null) {
            return event;
        }

        switch (StateAction.valueOf(userRequest.getStateAction())) {
            case CANCEL_REVIEW:
                event.setState(State.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(State.PENDING);
                break;
            case PUBLISH_EVENT:
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case REJECT_EVENT:
                event.setState(State.CANCELED);
                break;
        }

        return event;
    }

    private static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(FORMAT);
    }

    public static EventFullDto mapToEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(formatDate(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(formatDate(event.getEventDate()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .location(LocationMapper.mapToLocation(event.getLocationModel()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() != null ? formatDate(event.getPublishedOn()) : null)
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
                .eventDate(formatDate(event.getEventDate()))
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
