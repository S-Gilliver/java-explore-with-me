package ru.practicum.ewm.stats.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.stats.EndpointHitDto;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.stats.exception.BadRequestException;
import ru.practicum.ewm.stats.mapper.StatsMapper;
import ru.practicum.ewm.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Data
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto createEndpointHit(EndpointHitDto endpointHitDto) {
        log.debug("Entering createEndpointHit method with endpointHitDto: {}", endpointHitDto);
        EndpointHitDto result = StatsMapper.mapToEndpointHitDto(statsRepository
                .save(StatsMapper.mapToEndpointHit(endpointHitDto)));
        log.debug("Exiting createEndpointHit method with result: {}", result);
        return result;
    }

    @Override
    public List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        validateStartBeforeEnd(start, end);

        if (unique && !uris.isEmpty()) {
            return statsRepository.findByDateAndUrisAndUnique(start, end, uris);
        } else if (!unique && !uris.isEmpty()) {
            return statsRepository.findByDateAndUris(start, end, uris);
        } else if (unique) {
            return statsRepository.findByDateAndUnique(start, end);
        } else {
            return statsRepository.findByDate(start, end);
        }
    }

    private void validateStartBeforeEnd(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            log.error("Invalid time range: Start time ({}) is after end time ({})", start, end);
            throw new BadRequestException("Invalid time range: The start time must be before the end time");
        }
    }
}
