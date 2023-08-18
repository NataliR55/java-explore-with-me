package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventStateAction;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequestDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        checkNewEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));
        User user = userRepository.getUserById(userId);
        Category category = categoryRepository.getCategoryById(newEventDto.getCategory());
        Location location = getOrSaveLocation(newEventDto.getLocation());
        Event event = EventMapper.fromNewEventDtoToEvent(newEventDto, category, user, location);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Event with id: %d and Initiator id: %d is not exists", eventId, userId));
        checkNewEventDate(updateEventDto.getEventDate(), LocalDateTime.now().plusHours(2));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("Event id:%d.You can only change unpublished or canceled events.",
                    eventId));
        }
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getAnnotation() != null && !(updateEventDto.getAnnotation().isBlank())) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getDescription() != null && !(updateEventDto.getDescription().isBlank())) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventDto.getLocation()));
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
        if (updateEventDto.getCategory() != null) {
            event.setCategory(categoryRepository.getCategoryById(updateEventDto.getCategory()));
        }
        if (updateEventDto.getStateAction() != null) {
            setEventState(event, updateEventDto.getStateAction());
        }
        Map<Long, Long> views = statisticService.getStatsEvents(List.of(event));
        EventDto eventDto = EventMapper.toEventDto(eventRepository.save(event));
        eventDto.setViews(views.getOrDefault(event.getId(), 0L));
        setComfirmedRequests(List.of(eventDto));
        return eventDto;

    }


    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeStatusRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateDto) {
        Event event = eventRepository.getEventById(eventId);
        List<Request> requestsEvent = requestRepository.findAllByEventId(eventId);

        if (event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException("Limit of participants of event has been exceeded");
        }

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult(
                new ArrayList<>(), ArrayList < ParticipationRequestDto > ());


        List<Request> requestListUpdateStatus = new ArrayList<>();
        for (Request request : requestsEvent) {
            if (updateDto.getRequestIds().contains(request.getId())) {
                request.setStatus(updateDto.getStatus());
                requestListUpdateStatus.add(request);
            }
        }

        for (Request request : requestListUpdateStatus) {
            if (!request.getStatus().equals(RequestStatus.CANCELED)) {
                eventResultConstructor(eventRequestStatusUpdateResult, request, event);
            } else {
                throw new ConflictException("Ошибка.");
            }
        }

        requestRepository.saveAll(requestListUpdateStatus);
        return eventRequestStatusUpdateResult;
    }

    private Location getOrSaveLocation(LocationDto locationDto) {
        Location location = LocationMapper.toLocation(locationDto);
        return locationRepository.findFirstByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));
    }

    private void checkNewEventDate(LocalDateTime eventDateTime, LocalDateTime minDateTime) {
        if (eventDateTime != null && eventDateTime.isBefore(minDateTime)) {
            throw new ConflictException(String.format("EventDate must by %s later than %s", eventDateTime, minDateTime));
        }
    }

    private void checkStartIsBeforeEnd(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ConflictException(String.format("Date start %s is after date end %s", start, end));
        }
    }

    private void setEventState(Event event, EventStateAction stateAction) {
        switch (stateAction) {
            case CANCEL_REVIEW:
            case REJECT_EVENT:
                event.setState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            case PUBLISH_EVENT:
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                break;
            default:
                throw new ValidationException("Error set state!!");
        }
    }

}
