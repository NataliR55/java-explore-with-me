package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;


import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    /*
    private final EventAdminService eventAdminService;

    @PatchMapping("/{eventId}")
    EventFullDto update(@PathVariable Long eventId,
                        @Validated @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                        HttpServletRequest request) {
        return eventAdminService.update(eventId, updateEventAdminRequest, request);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    List<EventFullDto> get(@RequestParam(required = false) List<Long> users,
                           @RequestParam(required = false) List<String> states,
                           @RequestParam(required = false) List<Long> categories,
                           @RequestParam(required = false) String rangeStart,
                           @RequestParam(required = false) String rangeEnd,
                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                           @Positive @RequestParam(defaultValue = "10") Integer size,
                           HttpServletRequest request) {

        return eventAdminService.get(users, states, categories, rangeStart, rangeEnd, from, size, request);
    }
*/
    //todo delete !!!!!
    /*StatsClient statsClient;
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void addStats(@RequestBody @Valid EndpointHitDto endpointHitDto) {
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

     */
}