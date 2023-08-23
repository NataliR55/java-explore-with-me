package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
        List<ViewStatsDto> viewStatsDto = unique ? statsRepository.getViewStatsWithUniqueIp(start, end, uris) :
                statsRepository.getViewStatsWithAnyIp(start, end, uris);
        return viewStatsDto;
    }
}