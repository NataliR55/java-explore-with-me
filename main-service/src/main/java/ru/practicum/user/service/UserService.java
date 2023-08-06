package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    List<UserDto> getUser(Long[] userIds, Integer from, Integer size);

    void deleteUser(Long userId);
}
