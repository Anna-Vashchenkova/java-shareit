package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.SearchStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking saveNewBooking(LocalDateTime start, LocalDateTime end, Long itemId, Long userId);

    Booking updateBooking(long bookingId, Long userId, Boolean approved);

    Booking getBookingById(Long userId, long bookingId);

    List<Booking> getBookings(Long userId, SearchStatus state, int from, int size);

    List<Booking> getBookingsByOwner(Long userId, SearchStatus state);

    List<Booking> getBookingsForUser(Long itemId);
}
