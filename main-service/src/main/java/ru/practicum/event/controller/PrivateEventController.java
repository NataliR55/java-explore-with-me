package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequestDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto createEvent(@PathVariable(name = "userId") @Positive Long userId,
                             @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventUser(@PathVariable(name = "userId") @Positive Long userId,
                                        @PathVariable(name = "eventId") @Positive Long eventId,
                                        @RequestBody @Valid UpdateEventUserRequestDto updateEventUserRequestDto) {
        return eventService.updateEvent(userId, eventId, updateEventUserRequestDto);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeStatusRequest(@PathVariable(name = "userId") @Positive Long userId,
                                                              @PathVariable(name = "eventId") @Positive Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest updateEvents) {
        return eventService.changeStatusRequest(userId, eventId, updateEvents);
    }
}
