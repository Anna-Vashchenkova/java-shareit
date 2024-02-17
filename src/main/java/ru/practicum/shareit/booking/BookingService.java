package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.SearchStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking saveNewBooking(Long id, LocalDateTime start, LocalDateTime end, Long itemId , Long userId);

    Booking updateBooking(long bookingId, Long userId, Boolean approved);

    Booking getBookingById(long bookingId);

    List<Booking> getBookings(Long userId, SearchStatus state);

    List<Booking> getBookingsByOwner(Long userId, SearchStatus state);

    List<Booking> getBookingsForUser(Long itemId);
}
