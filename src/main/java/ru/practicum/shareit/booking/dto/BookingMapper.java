package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
             booking.getId(),
             booking.getStart(),
             booking.getEnd(),
             booking.getItem(),
             booking.getBooker(),
             booking.getStatus() == Status.WAITING
        );
    }
}