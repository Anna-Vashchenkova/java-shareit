package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.BookingMapper;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingOutcomeDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody BookingIncomeDto dto) {
        log.info("Получен запрос на добавление бронирования '{}' пользователем '{}'",dto, userId);
        return BookingMapper.toBookingDto(bookingService.saveNewBooking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                dto.getItemId(),
                userId
                ));
    }

    @PatchMapping("/{bookingId}")
    public BookingOutcomeDto approveBooking(@PathVariable("bookingId") long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление статуса бронирования с ID={}", bookingId);
        return BookingMapper.toBookingDto(bookingService.updateBooking(bookingId, userId, approved));
    }
}

