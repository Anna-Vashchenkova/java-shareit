package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User saveUser(User user);
    User updateUser(long userId, User user);

    User getUserById(long userId);

    void deleteUserById(long userId);
}
