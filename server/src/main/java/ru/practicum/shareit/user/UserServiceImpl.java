package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(Long id, String email, String name) {
        return repository.save(new User(id, email, name));
    }

    public User updateUser(Long userId, User user) {
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        if (user.getId() == null) {
            user.setId(userId);
        }
        User userUpdate = repository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с ID=" + userId + " не найден!"));
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            User userByEmail = repository.getUserByEmail(user.getEmail());
            if (userByEmail == null) {
                userUpdate.setEmail(user.getEmail());
            } else if (!userByEmail.getId().equals(userId)) {
                    throw new RuntimeException("Пользователь с таким email уже существует.");
            }
        }
        return repository.save(userUpdate);
    }

    @Override
    public User getUserById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new DataNotFoundException("Пользователь не найден"));
    }

    @Override
    public void deleteUserById(Long userId) {
        repository.deleteById(userId);
    }
}