package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    default Request getRequestById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException(String.format("Request with id: %s not exist!", id)));
    }

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    boolean existsByIdAndRequesterId(Long id, Long requesterId);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);
}
