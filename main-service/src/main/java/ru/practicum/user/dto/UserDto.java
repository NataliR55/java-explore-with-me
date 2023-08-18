package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.util.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @Null(groups = Marker.OnCreate.class)
    private Long id;
    @NotBlank(message = "Name must be filled!", groups = Marker.OnCreate.class)
    private String name;
    @NotBlank(message = "Email must be filled!", groups = Marker.OnCreate.class)
    @Email(message = "Email must have the format EMAIL!", groups = Marker.OnCreate.class)
    private String email;
}
