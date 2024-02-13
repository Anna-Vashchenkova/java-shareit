package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

public interface BookingService {
    Booking saveNewBooking(Long id, LocalDateTime start, LocalDateTime end, Item item, Long userId, Boolean status);
}
