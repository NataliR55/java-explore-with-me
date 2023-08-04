package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.Constants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsClient {
    @Value("${stats-server.url}")
    private String serverUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public void addStats(EndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.postForEntity(URI.create(serverUrl + "/hit"), requestEntity, EndpointHitDto.class);
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of("start", start.format(Constants.DATE_TIME_FORMATTER),
                "end", end.format(Constants.DATE_TIME_FORMATTER),
                "uris", uris,
                "unique", unique);
        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                serverUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                ViewStatsDto[].class, parameters);
        return response.getStatusCode().is2xxSuccessful() ? Arrays.stream(response.getBody())
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
