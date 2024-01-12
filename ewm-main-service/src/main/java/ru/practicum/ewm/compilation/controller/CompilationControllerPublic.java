package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.PublicCompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationControllerPublic {

    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET api with params: pinned = {}, from = {}, size = {}", pinned, from, size);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        return publicCompilationService.getCompilation(pinned, pageRequest);
    }

    @GetMapping("{compId}")
    public CompilationDto getCompilationById(@PathVariable @PositiveOrZero int compId) {
        log.info("GET api with param: compilationId = {}", compId);
        return publicCompilationService.getCompilationById(compId);
    }
}
