package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventMapper {
    private EventMapper() {
    }

    public static Event fromNewEventDtoToEvent(NewEventDto eventDto, Category category, User user, Location location) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(category)
                .description(eventDto.getDescription())
                .createdOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .eventDate(eventDto.getEventDate())
                .initiator(user)
                .location(location)
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit() == null ? 0 : eventDto.getParticipantLimit())
                .confirmedRequests(0L)
                .requestModeration(eventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(eventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .title(event.getTitle())
                .views(0L)
                .confirmedRequests(0L)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event, Long views, Long confirmedRequests) {
        EventFullDto eventFullDto = toEventFullDto(event);
        eventFullDto.setViews(views);
        eventFullDto.setConfirmedRequests(confirmedRequests);
        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event, Long views, Long confirmedRequests) {
        EventShortDto eventShortDto = toEventShortDto(event);
        eventShortDto.setViews(views);
        eventShortDto.setConfirmedRequests(confirmedRequests);
        return eventShortDto;
    }

    public static List<EventShortDto> toListEventShortDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public static List<EventShortDto> toListEventShortDto(List<Event> events, Map<Long, Long> views,
                                                          Map<Long, Long> comfirmedRequests) {
        List<EventShortDto> eventShorts = toListEventShortDto(events);
        if (views != null && !views.isEmpty()) {
            eventShorts.forEach(e -> e.setViews(views.get(e.getId())));
        }
        if (comfirmedRequests != null && !comfirmedRequests.isEmpty()) {
            eventShorts.forEach(e -> e.setConfirmedRequests(comfirmedRequests.getOrDefault(e.getId(), 0L)));
        }
        return eventShorts;
    }

    public static List<EventFullDto> toListEventFullDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public static List<EventFullDto> toListEventFullDto(List<Event> events, Map<Long, Long> views,
                                                        Map<Long, Long> comfirmedRequests) {
        List<EventFullDto> eventFulls = toListEventFullDto(events);
        if (views != null && !views.isEmpty()) {
            eventFulls.forEach(e -> e.setViews(views.get(e.getId())));
        }
        if (comfirmedRequests != null && !comfirmedRequests.isEmpty()) {
            eventFulls.forEach(e -> e.setConfirmedRequests(comfirmedRequests.getOrDefault(e.getId(), 0L)));
        }
        return eventFulls;
    }

    public static <T extends UpdateEventRequestDto> void updateEventRequestDto(T updateEventDto, Event event,
                                                                               Location location, Category category) {
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getAnnotation() != null && !(updateEventDto.getAnnotation().isBlank())) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getDescription() != null && !(updateEventDto.getDescription().isBlank())) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null && !(updateEventDto.getTitle().isBlank())) {
            event.setTitle(updateEventDto.getTitle());
        }
        if (location != null) {
            event.setLocation(location);
        }
        if (category != null) {
            event.setCategory(category);
        }
    }
}
