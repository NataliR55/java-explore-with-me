package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.model.enums.EventStateAction;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequestDto extends UpdateEventRequestDto {
    private EventStateAction stateAction;
}