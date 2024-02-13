package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;

    @Override
    public Booking saveNewBooking(Long id, LocalDateTime start, LocalDateTime end, Item item, Long userId, Boolean available) {
        User booker = userService.getUserById(userId);
        if (booker == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        Status status;
        if (available == null) {
            throw new ValidationException("В поле available не допустимое значение.");
        }
        if (available) {
            status = Status.WAITING;
        } else {
            status = Status.REJECTED; //здесь не знаю
        }
        return repository.save(new Booking(id, start, end, item, booker, status));
    }
}
