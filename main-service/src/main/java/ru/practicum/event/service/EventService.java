package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequestDto;
import ru.practicum.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequestDto updateEventDto);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequestDto updateEventDto);

    EventRequestStatusUpdateResult changeStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateDto);

    List<EventShortDto> getEventsUser(Long userId, Pageable page);

    EventFullDto getFullEventUser(Long userId, Long eventId);

    List<ParticipationRequestDto> getUserRequests(Long userId, Long eventId);

    List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);
}
