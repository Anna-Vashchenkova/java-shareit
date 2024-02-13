package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping() //После создания запрос находится в статусе WAITING — «ожидает подтверждения».
    public BookingDto saveNewUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody BookingDto dto) {
        log.info("Получен запрос на добавление бронирования '{}' пользователя '{}'",dto, userId);
        return BookingMapper.toBookingDto(bookingService.saveNewBooking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                dto.getItem(),
                userId,
                dto.getStatus()
                ));
    }
}
