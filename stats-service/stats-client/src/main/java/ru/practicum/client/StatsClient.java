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

import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        restTemplate.postForEntity(serverUrl.concat("/hit"), requestEntity, EndpointHitDto.class);
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String startStr = start.format(Constants.DATE_TIME_FORMATTER);
        String endStr = end.format(Constants.DATE_TIME_FORMATTER);
        Map<String, Object> parameters = Map.of("start", startStr,
                "end", endStr,
                "uris", uris != null ? String.join(",", uris) : "null",
                "unique", unique.toString());
        ViewStatsDto[] viewStatsDto = restTemplate.getForObject(
                serverUrl.concat("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                ViewStatsDto[].class, parameters);
        return viewStatsDto == null ? Collections.emptyList() : List.of(viewStatsDto);
    }
}