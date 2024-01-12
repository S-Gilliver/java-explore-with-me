package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.EventCompilationConnection;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.compilation.repository.EventCompilationConnectionRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    private final EventCompilationConnectionRepository eventCompilationConnectionRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CompilationDto postCompilation(NewCompilationDto compilationDto) {

        if (compilationRepository.existsByTitle(compilationDto.getTitle())) {
            throw new ConflictException(String.format("Compilation with title=%s was not found", compilationDto.getTitle()));
        }

        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        Compilation compilation = CompilationMapper.createCompilation(compilationDto);
        Compilation compilationFromDb = compilationRepository.save(compilation);

        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(new ArrayList<>(compilationDto.getEvents()));
            eventShortDtoList = events.stream().map(event -> EventMapper
                    .createEventShortDto(event, requestRepository
                            .countRequestByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)))
                    .collect(Collectors.toList());

            for (Integer eventId : compilationDto.getEvents()) {
                eventCompilationConnectionRepository
                        .save(new EventCompilationConnection(0, eventId, compilationFromDb.getId()));
            }
            return CompilationMapper.createCompilationDtoWithoutEventList(compilation, eventShortDtoList);
        }

        return CompilationMapper.createCompilationDtoWithoutEventList(compilationFromDb, eventShortDtoList);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCompilation(int compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compilationId));
        }
        compilationRepository.deleteById(compilationId);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CompilationDto patchCompilation(UpdateCompilationRequest updateCompilationRequest, int compilationId) {

        Compilation compilationFromDb = compilationRepository.findById(compilationId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Compilation with id=%d was not found", compilationId)));

        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilationFromDb.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilationFromDb.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getEvents() != null) {
            if (updateCompilationRequest.getEvents().isEmpty()) {
                compilationFromDb.setEvents(new ArrayList<>());
            } else {
                compilationFromDb.setEvents(eventRepository.findAllById(new ArrayList<>(updateCompilationRequest.getEvents())));
            }
        }

        return CompilationMapper
                .createCompilationDtoWithEventList(compilationRepository.save(compilationFromDb),
                        requestRepository);
    }
}

