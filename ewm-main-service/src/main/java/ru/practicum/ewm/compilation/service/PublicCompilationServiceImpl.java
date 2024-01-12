package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<CompilationDto> getCompilation(boolean pinned, PageRequest pageRequest) {
        if (pinned) {
            return compilationRepository.getAllByPinned(true, pageRequest).getContent().stream()
                    .map(event -> CompilationMapper.createCompilationDtoWithEventList(event, requestRepository))
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAll(pageRequest).getContent().stream()
                .map(event -> CompilationMapper.createCompilationDtoWithEventList(event, requestRepository))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public CompilationDto getCompilationById(int compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Compilation with id=%d was not found", compilationId)));
        return CompilationMapper.createCompilationDtoWithEventList(compilation, requestRepository);
    }
}

