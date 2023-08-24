package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.util.ConvertDataTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConvertDataTime.DATE_TIME_PATTERN)
    LocalDateTime eventDate;
    @NotNull
    LocationDto location;
    @NotNull
    Boolean paid = false;
    @NotNull
    @PositiveOrZero
    Integer participantLimit = 0;
    @NotNull
    Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}