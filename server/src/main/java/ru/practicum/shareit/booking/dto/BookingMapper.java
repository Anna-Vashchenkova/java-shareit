package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

public class BookingMapper {
    public static BookingOutcomeDto toBookingDto(Booking booking) {
        return new BookingOutcomeDto(
             booking.getId(),
             booking.getStart(),
             booking.getEnd(),
             ItemMapper.toItemDto(booking.getItem()),
             UserMapper.toUserDto(booking.getBooker()),
             booking.getStatus().name()
        );
    }
}
