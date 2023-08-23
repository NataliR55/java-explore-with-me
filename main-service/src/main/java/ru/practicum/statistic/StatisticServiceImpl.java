package ru.practicum.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatsClient statsClient;

    @Override
    public void addView(HttpServletRequest request) {
        String appName = "main-server";
        statsClient.addStats(EndpointHitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public Map<Long, Long> getStatsEvents(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> ids = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        LocalDateTime start = events.stream()
                .sorted(Comparator.comparing(Event::getCreatedOn))
                .map(Event::getCreatedOn)
                .findFirst().orElse(LocalDateTime.now());
        LocalDateTime end = LocalDateTime.now();
        String eventsUri = "/events/";
        List<String> uris = ids.stream()
                .map(id -> eventsUri + id)
                .collect(Collectors.toList());
        List<ViewStatsDto> views = statsClient.getViewStats(start, end, uris, true);
        Map<Long, Long> viewsMap = new HashMap<>();
        for (ViewStatsDto view : views) {
            String uri = view.getUri();
            viewsMap.put(Long.parseLong(uri.substring(eventsUri.length())), view.getHits());
        }
        return viewsMap;
    }
}
