package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User getUserByEmail(String email);

    User getUserById(Long userId);

    void deleteUserById(long userId);
}
