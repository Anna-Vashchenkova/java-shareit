package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking saveNewBooking(Long id, LocalDateTime start, LocalDateTime end, Long itemId, Long userId) {
        User booker = userService.getUserById(userId);
        if (booker == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        Status status = Status.WAITING;
        Item item = itemService.getItemById(itemId);
        if (item.getAvailable() != ru.practicum.shareit.item.model.Status.AVAILABLE) {
            throw new ValidationException("Вещь уже забронирована.");
        }
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Время начала бронирования не может быть позже окончания.");
        }
        /*if (available == null) {
            throw new ValidationException("В поле available не допустимое значение.");
        }
        if (available) {
            status = Status.WAITING;
        } else {
            status = Status.REJECTED; //здесь не знаю
        }*/
        return repository.save(new Booking(id, start, end, item, booker, status));
    }

    @Override
    public Booking updateBooking(long bookingId, Long userId, Boolean approved) {
        User booker = userService.getUserById(userId);
        if (booker == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new DataNotFoundException("Бронирование не найдено!"));
        Long itemIdFromBooking = booking.getItem().getId();
        Boolean itemValid = itemService.getItems(userId).stream().anyMatch(item -> item.getId().equals(itemIdFromBooking));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время бронирования уже истекло!");
        }
        if (booking.getBooker().getId() == userId) {
            if (!approved) {
                booking.setStatus(Status.CANCELED);
                log.info("Бронирование отменено.");
            } else {
                throw new DataNotFoundException("Подтвердить бронирование может только владелец вещи!");
            }
        } else if ((itemValid) && (!booking.getStatus().equals(Status.CANCELED))) {
            if (!booking.getStatus().equals(Status.WAITING)) {
                throw new ValidationException("Решение по бронированию уже принято");
            }
            if (approved) {
                booking.setStatus(Status.APPROVED);
                log.info("Пользователь с ID={} подтвердил бронирование с ID={}", userId, bookingId);
            } else {
                booking.setStatus(Status.REJECTED);
                log.info("Пользователь с ID={} отклонил бронирование с ID={}", userId, bookingId);
            }
        } else {
            if (booking.getStatus().equals(Status.CANCELED)) {
                throw new ValidationException("Бронирование было отменено!");
            } else {
                throw new ValidationException("Подтвердить бронирование может только владелец вещи!");
            }
        }
        return repository.save(booking);
    }

    @Override
    public Booking getBookingById(long bookingId) {
        return repository.findById(bookingId).orElseThrow(() -> new DataNotFoundException("Вещь с таким id не найдена.")
        );
    }

    @Override
    public List<Booking> getBookings(Status state, Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        return repository.getBookingByBooker_IdAndStatus(state, userId);
    }
}
