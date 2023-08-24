package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Name must be filled!")
    @Size(min = 2, max = 250, message = "Size name must be >=2 and <=250 characters")
    private String name;
    @NotBlank(message = "Email must be filled!")
    @Email(message = "Email must have the format EMAIL!")
    @Size(min = 6, max = 254, message = "Size email must be >=6 and <=254 characters")
    private String email;
}
