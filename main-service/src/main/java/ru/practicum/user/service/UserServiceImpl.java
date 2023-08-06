package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        return null;
    }

    @Override
    public List<UserDto> getUser(Long[] userIds, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public void deleteUser(Long userId) {
    }
}
