package ru.practicum.statistic;

import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface StatisticService {
    void addView(HttpServletRequest request);

    Map<Long, Long> getStatsEvents(List<Event> events);
}
