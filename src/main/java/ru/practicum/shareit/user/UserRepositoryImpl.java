package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static List<User> users = new ArrayList<>();
    private Long lastId = 1L;

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(generateUserId());
            users.add(user);
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        return users.stream().filter(user -> user.getEmail().equals(email)).findAny().orElse(null);
    }

    @Override
    public User getUserById(Long userId) {
        return users.stream()
                .filter(user -> user.getId() == userId)
                .findFirst().orElse(null);
    }

    @Override
    public void deleteUserById(long userId) {
        users.remove(getUserById(userId));
    }

    private Long generateUserId() {
        return lastId++;
    }

}

