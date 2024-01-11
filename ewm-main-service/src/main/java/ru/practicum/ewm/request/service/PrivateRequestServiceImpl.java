package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.enums.EventState;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ParticipationRequestDto postRequest(int userId, int eventId) {

        User userFromDb = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        Event eventFromDb = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(String.format("Request from user with id=%d to event with id=%d already exists", userId, eventId));
        }

        if (userId == eventFromDb.getInitiator().getId()) {
            throw new ConflictException(String.format("Can't create request. User with id=%d is owner of the event with %d", userId, eventId));
        }

        if (!eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("Can't create request. Event with id=%d is not published", eventId));
        }

        if (eventFromDb.getParticipantLimit() > 0 && requestRepository.countRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= eventFromDb.getParticipantLimit()) {
            throw new ConflictException(String.format("Can't create request. Event with id=%d is full", eventId));
        }

        Request request = RequestMapper.createRequest(userFromDb, eventFromDb);

        return RequestMapper.createParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<ParticipationRequestDto> getRequests(int userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        return requestRepository.getRequestsByRequesterId(userId).stream()
                .map(RequestMapper::createParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ParticipationRequestDto cancelRequest(int userId, int requestId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found", requestId)));

        if (request.getRequester().getId() != userId) {
            throw new BadRequestException("Can't cancel because you're not owner");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.createParticipationRequestDto(requestRepository.save(request));
    }
}
