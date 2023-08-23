package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.model.enums.EventSort;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.model.enums.EventStateAction;
import ru.practicum.request.model.enums.RequestStatus;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventStateActionUser;
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
import ru.practicum.util.ConvertDataTime;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
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
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("Event id:%d.You can only change PENDING or CANCELED events.",
                    eventId));
        }
        EventStateActionUser eventState = updateEventDto.getStateAction();
        if (eventState != null) {
            if (EventStateActionUser.CANCEL_REVIEW.equals(eventState)) {
                event.setState(EventState.CANCELED);
            } else if (EventStateActionUser.SEND_TO_REVIEW.equals(eventState)) {
                event.setState(EventState.PENDING);
            }
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
        setComfirmedRequestsEventFullDto(List.of(eventFullDto));
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
        if (updateEventDto.getStateAction() != null) {

            if (EventStateAction.PUBLISH_EVENT.equals(updateEventDto.getStateAction())) {
                if (EventState.PENDING.equals(event.getState())) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                } else {
                    throw new ConflictException(String.format(
                            "Event id:%d cannot PUBLISHED because it's not in the right state:%s ",
                            eventId, event.getState()));
                }
            } else if (EventStateAction.REJECT_EVENT.equals(updateEventDto.getStateAction())) {
                if (!EventState.PUBLISHED.equals(event.getState())) {
                    event.setState(EventState.CANCELED);
                } else {
                    throw new ConflictException(String.format(
                            "Event id:%d cannot CANCELED because it's not in the right state:%s ",
                            eventId, event.getState()));
                }
            }
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
        setComfirmedRequestsEventFullDto(List.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeStatusRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest eventRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException(String
                        .format("Event with id: %d and Initiator id: %d is not exists", eventId, userId)));
        if ((!event.getRequestModeration()) || (event.getParticipantLimit() == 0) || eventRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }
        if ((event.getParticipantLimit().longValue()) <= event.getConfirmedRequests()) {
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
        RequestStatus eventRequestStatus = RequestStatus.valueOf(eventRequest.getStatus().toUpperCase());
        if (RequestStatus.CONFIRMED.equals(eventRequestStatus)) {
            List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
            List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
            for (Request request : requests) {
                if (event.getConfirmedRequests() <= event.getParticipantLimit().longValue()) {
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
        } else if (RequestStatus.REJECTED.equals(eventRequestStatus)) {
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
        LocalDateTime[] rangeDate = new LocalDateTime[2];
        if ((rangeStart == null) && (rangeEnd == null)) {
            rangeDate[0] = ConvertDataTime.MIN_DATE_TIME;
            rangeDate[1] = ConvertDataTime.MAX_DATE_TIME;
        } else {
            rangeDate = checkDateTime(rangeStart, rangeEnd);
        }
        List<Event> events = eventRepository.getEventsAdmin(
                users, eventStates, categories, rangeDate[0], rangeDate[1], page);
        Map<Long, Long> views = statisticService.getStatsEvents(events);
        List<EventFullDto> eventFullDto = events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
        eventFullDto.forEach(e -> e.setViews(views.get(e.getId())));
        setComfirmedRequestsEventFullDto(eventFullDto);
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
        setComfirmedRequestsEventFullDto(List.of(eventFullDto));
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

    /**
     * это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
     * текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
     * если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
     * информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
     * информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     */
    @Override
    public List<EventShortDto> getAllEventsPublic(String text, List<Long> categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  boolean onlyAvailable, EventSort sort, Integer from, Integer size,
                                                  HttpServletRequest request) {
        LocalDateTime[] rangeDate = checkDateTime(rangeStart, rangeEnd);
        PageRequest pageRequest = getPageRequest(from, size);
        if (EventSort.EVENT_DATE.equals(sort)) {
            pageRequest.withSort(Sort.by("eventDate").descending());
        }
        Page<Event> eventsByParamPage;
        if (onlyAvailable) {
            eventsByParamPage = eventRepository.getAvailableEventsWithFilters(text, EventState.PUBLISHED, RequestStatus.CONFIRMED,
                    categories, paid, rangeDate[0], rangeDate[1], pageRequest);
        } else {
            eventsByParamPage = eventRepository.getAllEventsWithFilters(text, EventState.PUBLISHED, categories, paid,
                    rangeDate[0], rangeDate[1], pageRequest);
        }
        List<Event> events = eventsByParamPage.get().collect(Collectors.toList());
        statisticService.addView(request);
        Map<Long, Long> view = statisticService.getStatsEvents(events);
        List<EventShortDto> result = events.stream()
                .map(EventMapper::toEventShortDto)
                .peek(e -> e.setViews(view.get(e.getId())))
                .collect(Collectors.toList());
        if (EventSort.VIEWS.equals(sort)) {
            result.sort(Comparator.comparing(EventShortDto::getViews));
        }
        setComfirmedRequestsEventShortDto(result);
        return result;
    }

    @Override
    public EventFullDto getEventFullPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.getEventById(eventId);
        if ((event.getState() != null) && (!EventState.PUBLISHED.equals(event.getState()))) {
            throw new NotFoundException(String.format("Event id:%d is not PUBLISHED", eventId));
        }
        statisticService.addView(request);
        Map<Long, Long> views = statisticService.getStatsEvents(List.of(event));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(views.getOrDefault(event.getId(), 0L));
        setComfirmedRequestsEventFullDto(List.of(eventFullDto));
        return eventFullDto;
    }

    private void checkRequestStatus(List<Request> requests, List<RequestStatus> requestStatus) {
        for (Request request : requests) {
            if (requestStatus.stream().filter(status -> status.equals(request.getStatus()))
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
            throw new ValidationException(String.format("Old Event date-time %s must by later than %s",
                    eventDateTime, minDateTime));
        }
        if (newEventDateTime != null && newEventDateTime.isBefore(minDateTime)) {
            throw new ValidationException(String.format("New Event date-time %s must by later than %s",
                    newEventDateTime, minDateTime));
        }
    }

    private void setComfirmedRequestsEventFullDto(List<EventFullDto> events) {
        List<Long> ids = events.stream().map(EventFullDto::getId).collect(Collectors.toList());
        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(ids, RequestStatus.CONFIRMED);
        Map<Long, Long> eventIdToConfirmedCount = requests.stream()
                .collect(groupingBy(r -> r.getEvent().getId(), Collectors.counting()));
        events.forEach(event -> event.setConfirmedRequests(eventIdToConfirmedCount.getOrDefault(event.getId(), 0L)));
    }

    private void setComfirmedRequestsEventShortDto(List<EventShortDto> events) {
        List<Long> ids = events.stream().map(EventShortDto::getId).collect(Collectors.toList());
        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(ids, RequestStatus.CONFIRMED);
        Map<Long, Long> eventIdToConfirmedCount = requests.stream()
                .collect(groupingBy(r -> r.getEvent().getId(), Collectors.counting()));
        events.forEach(event -> event.setConfirmedRequests(eventIdToConfirmedCount.getOrDefault(event.getId(), 0L)));
    }

    private PageRequest getPageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    private LocalDateTime[] checkDateTime(LocalDateTime start, LocalDateTime end) {
        LocalDateTime[] newDateTime = new LocalDateTime[2];
        newDateTime[0] = start;
        newDateTime[1] = end;
        if ((start == null) && (end == null)) {
            newDateTime[0] = LocalDateTime.now();
            newDateTime[1] = ConvertDataTime.MAX_DATE_TIME;
            return newDateTime;
        }
        if ((start != null) && (end != null)) {
            if (start.isAfter(end)) {
                throw new ValidationException("The end of the event cannot be earlier than the beginning of the event");
            }
            return newDateTime;
        }
        if ((start == null) || (end == null)) {
            newDateTime[0] = (start == null) ? ConvertDataTime.MIN_DATE_TIME : start;
            newDateTime[1] = (end == null) ? ConvertDataTime.MAX_DATE_TIME : end;
        }
        return newDateTime;
    }
}


