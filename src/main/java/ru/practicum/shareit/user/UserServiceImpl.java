package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(User user) {
        if ((user.getEmail() == null) || (user.getEmail().isEmpty()) || (!user.getEmail().contains("@"))) {
            throw new ValidationException("В переданных данных " +
                    "электронная почта не может быть пустой и должна содержать символ @");
        }
        if (repository.getUserByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Пользователь с таким email уже существует.");
        }
        return repository.save(user);
    }

    public User updateUser(long userId, User user) {
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        User userUpdate = repository.getUserById(userId);
        if (userUpdate == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            User userByEmail = repository.getUserByEmail(user.getEmail());
            if (userByEmail == null) {
                userUpdate.setEmail(user.getEmail());
            } else if (userByEmail.getId() != userId) {
                    throw new RuntimeException("Пользователь с таким email уже существует.");
            }
        }
        return repository.save(userUpdate);
    }

    @Override
    public User getUserById(long userId) {
        return repository.getUserById(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        repository.deleteUserById(userId);
    }
}