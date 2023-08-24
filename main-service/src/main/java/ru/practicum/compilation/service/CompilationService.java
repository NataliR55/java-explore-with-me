package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateReq);

    void deleteCompilationById(Long compId);

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    Compilation getCompilationById(Long compId);

    CompilationDto getCompilationDtoById(Long compilationId);
}
