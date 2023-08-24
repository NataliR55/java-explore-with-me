package ru.practicum.request.mapper;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    private RequestMapper() {
    }

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .build();
    }

    public static List<ParticipationRequestDto> toListParticipationRequestDto(List<Request> requests) {
        return requests.stream().map(request -> toParticipationRequestDto(request)).collect(Collectors.toList());
    }
}
