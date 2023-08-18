package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        User user = UserMapper.newUserRequestToUser(newUserRequest);
        try {
            log.info("Create user {} ", newUserRequest);
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException(String.format("User with E-mail: %s or name %s already  exist",
                    newUserRequest.getEmail(), newUserRequest.getName()));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Incorrect request to new user " + newUserRequest);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUser(List<Long> userIds, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        if (userIds == null) {
            log.info("Get all users");
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            log.info("Get users by ids: {}", userIds);
            return userRepository.findByIdIn(userIds, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Delete user with id:{} ", id);
        userRepository.delete(userRepository.getUserById(id));
    }
}
