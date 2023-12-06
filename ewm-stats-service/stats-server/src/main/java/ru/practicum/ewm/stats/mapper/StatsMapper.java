package ru.practicum.ewm.stats.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.stats.EndpointHitDto;
import ru.practicum.ewm.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ofPattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatsMapper {

    private static final DateTimeFormatter FORMAT = ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EndpointHit mapToEndpointHit(EndpointHitDto endpointHitDto) {
        Objects.requireNonNull(endpointHitDto, "endpointHitDto must not be null");
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .created(LocalDateTime.parse(endpointHitDto.getTimestamp(), FORMAT))
                .build();
    }

    public static EndpointHitDto mapToEndpointHitDto(EndpointHit endpointHit) {
        Objects.requireNonNull(endpointHit, "endpointHit must not be null");
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getCreated().format(FORMAT))
                .build();
    }
}
