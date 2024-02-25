package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;

    @Override
    public ItemRequest addNewRequest(Long userId, String description) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        if (description == null) {
            throw new ValidationException("Описание запроса не может быть пустым");
        }
        return repository.save(new ItemRequest(null, description, user, LocalDateTime.now()));
    }

    @Override
    public ItemRequest getRequestById(Long userId, Long requestId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        ItemRequest result = repository.findById(requestId).orElse(null);
        if (result == null) {
            throw new DataNotFoundException("Запрос с таким id не найден.");
        }
        return result;
    }

    @Override
    public List<ItemRequest> getRequests(Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        return repository.findAllByUserId(userId);
    }
}
