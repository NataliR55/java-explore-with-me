package ru.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.exception.NotFoundException;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);

    default Compilation getCompilationById(Long id) {
        return findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id: %d is not exists!", id)));
    }
}
