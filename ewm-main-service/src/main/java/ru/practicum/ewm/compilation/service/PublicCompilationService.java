package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilation(boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);
}
