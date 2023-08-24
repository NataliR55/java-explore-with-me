package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.enums.RequestStatus;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    boolean existsByIdAndRequesterId(Long id, Long requesterId);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByIdIsIn(List<Long> ids);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus requestStatus);
}
