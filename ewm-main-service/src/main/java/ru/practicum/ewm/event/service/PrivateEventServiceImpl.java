package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.CommentDto;
import ru.practicum.ewm.event.dto.CommentRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.mapper.CommentMapper;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.event.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EventFullDto postEvent(NewEventDto newEventDto, int userId) {

        if (LocalDateTime.now().plusHours(2).isAfter(newEventDto.getEventDate())) {
            throw new BadRequestException("Field eventDate:" + newEventDto.getEventDate());
        }

        User userFromDb = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        Category categoryFromDb = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", newEventDto.getCategory())));

        Event event = EventMapper.createEvent(newEventDto, userFromDb, categoryFromDb);

        Event eventFromDb = eventRepository.save(event);

        return EventMapper.createEventFullDto(eventFromDb, requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<EventShortDto> getUserEvents(int userId, PageRequest pageRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        return eventRepository.getByInitiatorId(userId, pageRequest).stream()
                .map(event -> EventMapper.createEventShortDto(event, requestRepository.countRequestByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public EventFullDto getEventById(int userId, int eventId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        Event eventFromDb = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));


        return EventMapper.createEventFullDto(eventFromDb, requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EventFullDto patchEvent(UpdateEventUserRequest updateEventUserRequest, int userId, int eventId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        Event eventFromDb = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    eventFromDb.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    eventFromDb.setState(EventState.PENDING);
                    break;
                default:
                    throw new BadRequestException("Invalid action: " + eventFromDb);
            }
        }

        if (updateEventUserRequest.getEventDate() != null) {
            if (LocalDateTime.now().plusHours(2).isAfter(updateEventUserRequest.getEventDate())) {
                throw new BadRequestException("Field eventDate");
            }
            eventFromDb.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            eventFromDb.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null && updateEventUserRequest.getCategory() != (eventFromDb.getCategory().getId())) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", updateEventUserRequest.getCategory())));
            eventFromDb.setCategory(category);
        }

        if (updateEventUserRequest.getDescription() != null) {
            eventFromDb.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getLocation() != null) {
            eventFromDb.setLocation(updateEventUserRequest.getLocation());
        }

        if (updateEventUserRequest.getPaid() != null) {
            eventFromDb.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getTitle() != null) {
            eventFromDb.setTitle(updateEventUserRequest.getTitle());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            eventFromDb.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }


        return EventMapper.createEventFullDto(eventRepository.save(eventFromDb), requestRepository.countRequestByEventIdAndStatus(eventFromDb.getId(), RequestStatus.CONFIRMED));

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<ParticipationRequestDto> getRequestsInEvent(int userId, int eventId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        return requestRepository.getRequestsByEventId(eventId).stream()
                .map(RequestMapper::createParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EventRequestStatusUpdateResult patchRequests(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, int userId, int eventId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        List<Request> requests = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());

        switch (eventRequestStatusUpdateRequest.getStatus()) {
            case REJECTED:
                for (Request request : requests) {
                    if (request.getStatus() == RequestStatus.CONFIRMED) {
                        throw new ConflictException("Cannot reject an already confirmed request.");
                    }
                    request.setStatus(RequestStatus.REJECTED);
                }
                break;
            case CONFIRMED:
                for (Request request : requests) {

                    if (!request.getEvent().getRequestModeration() || request.getEvent().getParticipantLimit() == 0) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        continue;
                    }

                    if (requestRepository.countRequestByEventIdAndStatus(request.getEvent().getId(), RequestStatus.CONFIRMED) >= request.getEvent().getParticipantLimit()) {
                        request.setStatus(RequestStatus.REJECTED);
                        throw new ConflictException("Cant accept request.");
                    }

                    request.setStatus(RequestStatus.CONFIRMED);

                }
                break;
        }

        return RequestMapper.createEventRequestStatusUpdateResult(requestRepository.saveAll(requests));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CommentDto postComment(CommentRequest commentRequest, int userId, int eventId) {

        User userFromDb = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        Event eventFromDb = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You can comment only published events");
        }

        Comment comment = CommentMapper.createComment(commentRequest, eventFromDb, userFromDb);

        return CommentMapper.createCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CommentDto patchComment(CommentRequest commentRequest, int userId, int eventId, int commentId) {


        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        Comment commentFromDb = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (commentFromDb.getAuthor().getId() != userId) {
            throw new ConflictException(String.format("User with id=%d is not the author of the comment with id=%d", userId, commentId));
        }

        if (commentRequest.getText() != null) {
            commentFromDb.setText(commentRequest.getText());
        }

        return CommentMapper.createCommentDto(commentRepository.save(commentFromDb));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteComment(int userId, int eventId, int commentId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        Comment commentFromDb = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (commentFromDb.getAuthor().getId() != userId) {
            throw new ConflictException(String.format("User with id=%d is not the author of the comment with id=%d", userId, commentId));
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<CommentDto> getComments(int userId, int eventId, PageRequest pageRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        return commentRepository.findAll(pageRequest).stream()
                .map(CommentMapper::createCommentDto)
                .collect(Collectors.toList());
    }
}

