package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {
    public static ItemOutcomeDto toItemDto(Item item) {
        return new ItemOutcomeDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable() == Status.AVAILABLE, UserMapper.toUserDto(item.getOwner()),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static ItemOutcomeInfoDto toItemInfoDto(Item item,
                                                             List<Booking> bookings,
                                                             List<ItemOutcomeInfoDto.CommentDto> comments) {
        Booking lastBooking = bookings.stream()
                .filter(b -> ((b.getItem().getId() == item.getId())
                        &&
                        (b.getStart().isBefore(LocalDateTime.now())) || (b.getEnd().isBefore(LocalDateTime.now()))))
                .findFirst().orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(b -> ((b.getItem().getId() == item.getId())
                        &&
                        (b.getStart().isAfter(LocalDateTime.now()))))
                .findFirst().orElse(null);
        return new ItemOutcomeInfoDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable() == Status.AVAILABLE,
                UserMapper.toUserDto(item.getOwner()),
                item.getRequest() != null ? item.getRequest().getId() : null,
                new ItemOutcomeInfoDto.BookingDto(
                        lastBooking.getId(),
                        lastBooking.getBooker().getId(),
                        lastBooking.getStart(),
                        lastBooking.getEnd()),
                new ItemOutcomeInfoDto.BookingDto(
                        nextBooking.getId(),
                        nextBooking.getBooker().getId(),
                        nextBooking.getStart(),
                        nextBooking.getEnd()), comments);
    }
}