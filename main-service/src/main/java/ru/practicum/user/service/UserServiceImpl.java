package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

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
            throw new DataIntegrityViolationException(String.format("User with E-mail: %s or name %s already  exist",
                    newUserRequest.getEmail(), newUserRequest.getName()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUser(List<Long> userIds, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        if (userIds == null) {
            log.info("Get all users");
            return UserMapper.toListUserDto(userRepository.findAll(pageable).toList());
        } else {
            log.info("Get users by ids: {}", userIds);
            return UserMapper.toListUserDto(userRepository.findByIdIn(userIds, pageable));
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Delete user with id:{} ", id);
        userRepository.delete(getUserById(id));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id: %s not exist!", userId)));
    }
}