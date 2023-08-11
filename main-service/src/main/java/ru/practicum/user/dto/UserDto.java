package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Name must be filled!")
    private String name;
    @NotBlank(message = "Email must be filled!")
    @Email(message = "Email must have the format EMAIL!")
    private String email;
}
