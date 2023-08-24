package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.model.enums.EventStateActionUser;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequestDto extends UpdateEventRequestDto {
    private EventStateActionUser stateAction;
}