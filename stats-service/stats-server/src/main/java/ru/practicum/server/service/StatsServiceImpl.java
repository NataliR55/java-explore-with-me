package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.mapper.ViewStatsMapper;
import ru.practicum.server.model.ViewStats;
import ru.practicum.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public EndpointHitDto addStats(EndpointHitDto endpointHitDto) {
        return EndpointHitMapper.toDto(statsRepository.save(EndpointHitMapper.fromDto(endpointHitDto)));
    }

    @Override
    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new ValidationException("End time cannot be early than Start time!!!");
        }
        List<ViewStats> viewStats = unique ? statsRepository.getViewStatsWithUniqueIp(start, end, uris) :
                statsRepository.getViewStatsWithAnyIp(start, end, uris);
        return viewStats.stream().map(ViewStatsMapper::toDto).collect(Collectors.toList());
    }
}