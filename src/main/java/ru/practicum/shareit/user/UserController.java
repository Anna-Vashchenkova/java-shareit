package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public User saveNewUser(@RequestBody User user) {
        log.info("Получен запрос на добавление пользователя '{}'", user);
        return userService.saveUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable("userId") Long userId, @RequestBody User user) {
        log.info("Получен запрос на обновление данных пользователя '{}'", userId);
        return userService.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос - показать данные пользователя '{}'", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос - удалить данные пользователя '{}'", userId);
         userService.deleteUserById(userId);
    }
}