package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.SearchStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
                                         @Valid @RequestBody BookingIncomeDto dto) {
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

    @GetMapping("/{bookingId}")
    public BookingOutcomeDto getBookingById(@PathVariable("bookingId") long bookingId) {
        log.info("Получен запрос на получение информации о бронировании с ID={}", bookingId);
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId));
    }

    @GetMapping()
    public List<BookingOutcomeDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam (name = "state", defaultValue = "ALL") String stateParam) {
        log.info("Получен запрос на получение " +
                "списка бронирований пользователя с ID={} с параметром STATE={}", userId, stateParam);
        SearchStatus state = SearchStatus.valueOf(stateParam);
        if (state == null) {
            throw new IllegalArgumentException("Неизвестный статус бронирования");
        }
        return bookingService.getBookings(userId, state).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingOutcomeDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam (name = "state", defaultValue = "ALL") String stateParam) {
        log.info("Получен запрос на получение " +
                "списка бронирований владельцем вещи с ID={} с параметром STATE={}", userId, stateParam);
        SearchStatus state = SearchStatus.valueOf(stateParam);
        if (state == null) {
            throw new IllegalArgumentException("Неизвестный статус бронирования");
        }
        return bookingService.getBookingsByOwner(userId, state).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}