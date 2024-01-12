package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilation(boolean pinned, PageRequest pageRequest);

    CompilationDto getCompilationById(int compId);
}
