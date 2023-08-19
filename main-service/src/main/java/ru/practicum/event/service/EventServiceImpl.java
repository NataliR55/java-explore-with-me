package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventStateAction;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.dto.*;
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
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.statistic.StatisticService;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatisticService statisticService;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        checkNewEventDate(null, newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));
        User user = userRepository.getUserById(userId);
        Category category = categoryRepository.getCategoryById(newEventDto.getCategory());
        Location location = getOrSaveLocation(newEventDto.getLocation());
        Event event = EventMapper.fromNewEventDtoToEvent(newEventDto, category, user, location);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    /**
     * изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
     * дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
     */
    @Override
    @Transactional
    public EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequestDto updateEventDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Event with id: %d and Initiator id: %d is not exists", eventId, userId)));
        checkNewEventDate(event.getEventDate(), updateEventDto.getEventDate(), LocalDateTime.now().plusHours(2));
        if (!((event.getState().equals(EventState.PENDING)) || (event.getState().equals(EventState.CANCELED)))) {
            throw new ConflictException(String.format("Event id:%d.You can only change PENDING or CANCELED events.",
                    eventId));
        }
        if (updateEventDto.getStateAction() != null) {
            setEventState(event, updateEventDto.getStateAction());
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
        Map<Long, Long> views = statisticService.getStatsEvents(List.of(event));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(views.getOrDefault(event.getId(), 0L));
        setComfirmedRequests(List.of(eventFullDto));
        return eventFullDto;
    }

    /**
     * дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
     */
    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequestDto updateEventDto) {

        Event event = eventRepository.getEventById(eventId);
        checkNewEventDate(event.getEventDate(), updateEventDto.getEventDate(), LocalDateTime.now().plusHours(1));

        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException(String.format("Event id:%d.You can only change unpublished or canceled events.",
                    eventId));
        }
        EventStateAction eventStateAction = updateEventDto.getStateAction();
        if (eventStateAction != null) {
            if ((eventStateAction.equals(EventStateAction.PUBLISH_EVENT))
                    && (!event.getState().equals(EventState.PENDING))) {
                throw new ConflictException(String.format(
                        "Event with id: %d can be published only if it is state=PENDING", eventId));
            }
            if ((eventStateAction.equals(EventStateAction.REJECT_EVENT)
                    && (!event.getState().equals(EventState.PUBLISHED)))) {
                throw new ConflictException(String.format(
                        "Event with id: %d can be REJECTED only if it is state not PUBLISHED", eventId));
            }
            setEventState(event, eventStateAction);
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
        Map<Long, Long> views = statisticService.getStatsEvents(List.of(event));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(views.getOrDefault(event.getId(), 0L));
        setComfirmedRequests(List.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeStatusRequest(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException(String
                        .format("Event with id: %d and Initiator id: %d is not exists", eventId, userId)));
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0 || eventRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }
        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ConflictException(String.format("Limit on requests for the event with id:%d has been reached",
                    eventId));
        }
        if (event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException(String.format("Limit of participants of event id:%d has been reached", eventId));
        }
        List<Request> requests = requestRepository.findAllByIdIsIn(eventRequest.getRequestIds());
        checkRequestStatus(requests, List.of(RequestStatus.PENDING));

        long countConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(countConfirmed);
        RequestStatus eventRequestStatus = RequestStatus.valueOf(eventRequest.getStatus().toLowerCase());
        if (eventRequestStatus.equals(RequestStatus.CONFIRMED)) {
            List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
            List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
            for (Request request : requests) {
                if (event.getConfirmedRequests() <= event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
                    requestRepository.save(request);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
                    requestRepository.save(request);
                }
            }
            eventRepository.save(event);
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        } else if (eventRequestStatus.equals(RequestStatus.REJECTED)) {
            return new EventRequestStatusUpdateResult(List.of(), addRejectedRequests(requests, eventRequestStatus));
        }
        return new EventRequestStatusUpdateResult(List.of(), List.of());
    }

    @Override
    public List<EventShortDto> getEventsUser(Long userId, Pageable page) {
        userRepository.existsUserById(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        Map<Long, Long> views = statisticService.getStatsEvents(events);
        List<EventShortDto> eventShortDto = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        eventShortDto.forEach(e -> e.setViews(views.getOrDefault(e.getId(), 0L)));
        return eventShortDto;
    }

    @Override
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page) {
        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream()
                    .map(s -> s.toUpperCase().trim())
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }
        List<Event> events = eventRepository.getEventsWithUsersStatesCategoriesDateTime(
                users, eventStates, categories, rangeStart, rangeEnd, page);
        Map<Long, Long> views = statisticService.getStatsEvents(events);
        List<EventFullDto> eventFullDto = events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
        eventFullDto.forEach(e -> e.setViews(views.get(e.getId())));
        setComfirmedRequests(eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto getFullEventUser(Long userId, Long eventId) {
        userRepository.existsUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id:%d not have event with id: %d", userId, eventId)));
        Map<Long, Long> views = statisticService.getStatsEvents(List.of(event));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(views.getOrDefault(event.getId(), 0L));
        setComfirmedRequests(List.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException(String.format("User with id:%d not have event with id: %d", userId, eventId));
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    private void checkRequestStatus(List<Request> requests, List<RequestStatus> requestStatus) {
        for (Request request : requests) {
            if (requestStatus.stream().filter(status -> request.getStatus().equals(status))
                    .findAny().orElse(null) == null) {
                throw new ConflictException(String.format(
                        "Status request with id:%d cannot change because the current status:%s ",
                        request.getId(), request.getStatus()));
            }
        }
    }

    private List<ParticipationRequestDto> addRejectedRequests(List<Request> requests, RequestStatus requestStatus) {
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (Request request : requests) {
            request.setStatus(requestStatus);
            requestRepository.save(request);
            rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
        }
        return rejectedRequests;
    }

    private Location getOrSaveLocation(LocationDto locationDto) {
        Location location = LocationMapper.toLocation(locationDto);
        return locationRepository.findFirstByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));
    }

    private void checkNewEventDate(LocalDateTime eventDateTime, LocalDateTime newEventDateTime, LocalDateTime minDateTime) {
        if (eventDateTime != null && eventDateTime.isBefore(minDateTime)) {
            throw new ConflictException(String.format("Old Event date-time %s must by later than %s",
                    eventDateTime, minDateTime));
        }
        if (eventDateTime != null && newEventDateTime.isBefore(minDateTime)) {
            throw new ConflictException(String.format("New Event date-time %s must by later than %s",
                    newEventDateTime, minDateTime));
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
                event.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                break;
            default:
                throw new ValidationException("Error set state!!");
        }
    }

    private void setComfirmedRequests(List<EventFullDto> events) {
        List<Long> ids = events.stream().map(EventFullDto::getId).collect(Collectors.toList());
        List<Request> requestList = requestRepository.findAllByEventIdInAndStatus(ids, RequestStatus.CONFIRMED);
        Map<Long, Long> eventIdToConfirmedCount = requestList.stream()
                .collect(groupingBy(r -> r.getEvent().getId(), Collectors.counting()));
        events.forEach(event -> event.setConfirmedRequests(eventIdToConfirmedCount.getOrDefault(event.getId(), 0L)));
    }
}
