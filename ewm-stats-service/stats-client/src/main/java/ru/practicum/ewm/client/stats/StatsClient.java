package ru.practicum.ewm.client.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.stats.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

@Service
public class StatsClient extends BaseClient {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String START = "start";
    private static final String END = "end";

    @Autowired
    public StatsClient(@Value("http://stats-server:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postEndpointHit(EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        String stringStart = start.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        String stringEnd = end.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));


        Map<String, Object> parameters = Map.of(
                START, stringStart,
                END, stringEnd
        );

        StringJoiner pathBuilder = new StringJoiner("&", "/stats?start={start}&end={end}", "");
        if (!uris.isEmpty()) {
            uris.forEach(uri -> pathBuilder.add("&uris=" + uri));
        }
        if (Objects.nonNull(unique)) {
            pathBuilder.add("&unique=" + unique);
        }
        String path = pathBuilder.toString();
        return makeAndSendRequest(HttpMethod.GET, path, null, parameters, null);
    }
}
