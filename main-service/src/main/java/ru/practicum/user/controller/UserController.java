package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Add user {} ", userDto);
        return userService.addUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUser(@RequestParam(name = "ids", required = false) Long[] userIds,
                                 @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                 @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.debug("List users {}", userIds);
        return userService.getUser(userIds, from, size);
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Delete user with id:{} ", userId);
        userService.deleteUser(userId);
    }
}
