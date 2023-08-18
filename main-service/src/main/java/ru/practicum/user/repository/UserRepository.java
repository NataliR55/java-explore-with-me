package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    default User getUserById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id: %s not exist!", id)));
    }

    default void existsUserById(Long id) {
        if (!existsById(id)) {
            throw new NotFoundException(String.format("User with id: %s not exist!", id));
        }
    }

    List<User> findByIdIn(List<Long> ids, Pageable pageable);
}
