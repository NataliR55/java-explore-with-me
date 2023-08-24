package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.enums.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User requester = getUserById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id: %d is not exists!", eventId)));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(String.format("Request with user id: %d and event id: %d already exists!",
                    userId, eventId));
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Initiator of event cannot add a request to his event!");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("Event id:%d has not been published yet.", eventId));
        }
        Integer participantLimit = event.getParticipantLimit();
        if ((participantLimit != 0) && (participantLimit <= requestRepository.countByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED))) {
            throw new ConflictException(String.format("The limit of participants event id: %d has been reached.",
                    eventId));
        }
        Request request = Request.builder()
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .requester(requester)
                .event(event)
                .status(!event.getRequestModeration() || event.getParticipantLimit() == 0
                        ? RequestStatus.CONFIRMED : RequestStatus.PENDING)
                .build();
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        existsUserById(userId);
        if (!requestRepository.existsByIdAndRequesterId(requestId, userId)) {
            throw new ConflictException(String.format("Request with request id: %d and user id: %d is not exists!",
                    requestId, userId));
        }
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id: %s not exist!", requestId)));

        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        existsUserById(userId);
        return RequestMapper.toListParticipationRequestDto(requestRepository.findAllByRequesterId(userId));
    }

    private void existsUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id: %s not exist!", userId));
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id: %s not exist!", userId)));
    }
}