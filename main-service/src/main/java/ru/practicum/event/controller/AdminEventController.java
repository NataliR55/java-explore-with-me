package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.Constants;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping()
    public Collection<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                              @RequestParam(required = false) List<String> states,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        PageRequest page = PageRequest.of(from, size);
        return eventService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, page);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable(name = "eventId") @Positive Long eventId,
                                         @RequestBody @Valid UpdateEventAdminRequestDto updateEventAdminRequest) {
        return eventService.updateEventAdmin(eventId, updateEventAdminRequest);
    }
}