package ru.practicum.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatsClient statsClient;
    @Value("${application.name}")
    private final String appName;

    @Override
    public void addView(HttpServletRequest request) {
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
            return null;
        }
        List<Long> ids = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        LocalDateTime start = events.stream()
                .sorted(Comparator.comparing(Event::getCreatedOn))
                .map(Event::getCreatedOn)
                .findFirst().orElse(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        LocalDateTime end = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
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
