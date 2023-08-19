package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.enums.EventState;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    default Event getEventById(Long id) {
        return findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Event with id: %d is not exists!", id)));
    }

    default void eventExists(Long id) {
        if (!existsById(id)) {
            throw new NotFoundException(String.format("Event with id: %d is not exists!", id));
        }
    }

    boolean existsByCategory(Category category);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Set<Event> findAllByIdIn(Set<Long> eventIds);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("select e from Event e " +
            "where (:#{#users == null} = true or e.initiator.id in :users) " +
            "and (:#{#states == null} = true or e.state in :states) " +
            "and (:#{#categories == null} = true or e.category.id in :categories) " +
            "and (:#{#rangeStart == null} = true or e.eventDate >= :rangeStart) " +
            "and (:#{#rangeEnd == null} = true or e.eventDate <= :rangeEnd)")
    List<Event> getEventsWithUsersStatesCategoriesDateTime(@Param("users") List<Long> users,
                                                           @Param("states") List<EventState> states,
                                                           @Param("categories") List<Long> categories,
                                                           @Param("rangeStart") LocalDateTime rangeStart,
                                                           @Param("rangeEnd") LocalDateTime rangeEnd,
                                                           Pageable pageable);

    @Query("select e from Event e " +
            "where ((:#{#text == null} = true or LOWER(e.annotation) like LOWER(concat('%', :text, '%'))) " +
            "or (:#{#text == null} = true or LOWER(e.description) like LOWER(concat('%', :text, '%')))) " +
            "and (:#{#state == null} = true or e.state = :state) " +
            "and (:#{#categories == null} = true or e.category.id in :categories) " +
            "and (:#{#paid == null} = true or e.paid = :paid) " +
            "and (:#{#rangeStart == null} = true or e.eventDate >= :rangeStart) " +
            "and (:#{#rangeEnd == null} = true or e.eventDate <= :rangeEnd) " +
            "order by e.eventDate desc")
    List<Event> getAvailableEventsWithFiltersDateSorted(@Param("text") String text,
                                                        @Param("state") EventState state,
                                                        @Param("categories") List<Long> categories,
                                                        @Param("paid") Boolean paid,
                                                        @Param("rangeStart") LocalDateTime rangeStart,
                                                        @Param("rangeEnd") LocalDateTime rangeEnd,
                                                        Pageable pageable);

    @Query("select e from Event e " +
            "where ((:#{#text == null} = true or LOWER(e.annotation) like LOWER(concat('%', :text, '%'))) " +
            "or (:#{#text == null} = true or LOWER(e.description) like LOWER(concat('%', :text, '%')))) " +
            "and (:#{#state == null} = true or e.state = :state) " +
            "and (:#{#categories == null} = true or e.category.id in :categories) " +
            "and (:#{#paid == null} = true or e.paid = :paid) " +
            "and (:#{#rangeStart == null} = true or e.eventDate >= :rangeStart) " +
            "and (:#{#rangeEnd == null} = true or e.eventDate <= :rangeEnd) ")
    List<Event> getAvailableEventsWithFilters(@Param("text") String text,
                                              @Param("state") EventState state,
                                              @Param("categories") List<Long> categories,
                                              @Param("paid") Boolean paid,
                                              @Param("rangeStart") LocalDateTime rangeStart,
                                              @Param("rangeEnd") LocalDateTime rangeEnd,
                                              Pageable pageable);

    @Query("select e from Event e " +
            "where ((:#{#text == null} = true or LOWER(e.annotation) like LOWER(concat('%', :text, '%'))) " +
            "or (:#{#text == null} = true or LOWER(e.description) like LOWER(concat('%', :text, '%')))) " +
            "and (:#{#state == null} = true or e.state = :state) " +
            "and (:#{#categories == null} = true or e.category.id in :categories) " +
            "and (:#{#paid == null} = true or e.paid = :paid) " +
            "and (:#{#rangeStart == null} = true or e.eventDate >= :rangeStart) " +
            "and (:#{#rangeEnd == null} = true or e.eventDate <= :rangeEnd) " +
            "order by e.eventDate desc")
    List<Event> getAllEventsWithFiltersDateSorted(@Param("text") String text,
                                                  @Param("state") EventState state,
                                                  @Param("categories") List<Long> categories,
                                                  @Param("paid") Boolean paid,
                                                  @Param("rangeStart") LocalDateTime rangeStart,
                                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                                  Pageable pageable);

    @Query("select e from Event e " +
            "where ((:#{#text == null} = true or LOWER(e.annotation) like LOWER(concat('%', :text, '%'))) " +
            "or (:#{#text == null} = true or LOWER(e.description) like LOWER(concat('%', :text, '%')))) " +
            "and (:#{#state == null} = true or e.state = :state) " +
            "and (:#{#categories == null} = true or e.category.id in :categories) " +
            "and (:#{#paid == null} = true or e.paid = :paid) " +
            "and (:#{#rangeStart == null} = true or e.eventDate >= :rangeStart) " +
            "and (:#{#rangeEnd == null} = true or e.eventDate <= :rangeEnd)")
    List<Event> getAllEventsWithFilters(@Param("text") String text,
                                        @Param("state") EventState state,
                                        @Param("categories") List<Long> categories,
                                        @Param("paid") Boolean paid,
                                        @Param("rangeStart") LocalDateTime rangeStart,
                                        @Param("rangeEnd") LocalDateTime rangeEnd,
                                        Pageable pageable);

}
