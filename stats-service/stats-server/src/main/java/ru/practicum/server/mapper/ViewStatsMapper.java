package ru.practicum.server.mapper;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.ViewStats;

public class ViewStatsMapper {
    public static ViewStatsDto toDto(final ViewStats viewStats){
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }
}
