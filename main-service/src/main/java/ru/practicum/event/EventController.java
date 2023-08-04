package ru.practicum.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.Constants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventController {
    StatsClient statsClient;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void addStats(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Add addStats {}", endpointHitDto);
        statsClient.addStats(endpointHitDto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getViewStats(
            @RequestParam(name = "start") @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN) LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN) LocalDateTime end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        return statsClient.getViewStats(start, end, uris, unique);
    }
}
