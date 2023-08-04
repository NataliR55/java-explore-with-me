package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.server.model.ViewStats(eh.app, eh.uri, count(eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR eh.uri in (:uris)) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getViewStatsWithAnyIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.server.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE (eh.timestamp BETWEEN :start AND :end) " +
            "AND (:uris IS NULL OR eh.uri in :uris) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC")
    List<ViewStats> getViewStatsWithUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                             @Param("uris") List<String> uris);
}
