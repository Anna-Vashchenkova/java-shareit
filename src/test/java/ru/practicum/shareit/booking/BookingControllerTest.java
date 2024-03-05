package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @InjectMocks
    BookingController bookingController;
    @Mock
    BookingService bookingService;
    private User booker;
    private User owner;
    private UserDto bookerDto;
    private UserDto ownerDto;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private BookingIncomeDto bookingIncomeDto;
    private BookingOutcomeDto bookingOutcomeDto;
    private BookingOutcomeDto bookingOutcomeDto2;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "user1@mail.ru", "user1");
        owner = new User(2L, "user2@mail.ru", "user2");
        bookerDto = new UserDto(1L, "user1@mail.ru", "user1");
        ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
        created = LocalDateTime.now();
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        request1 = new ItemRequest(1L, "запрос1", booker, created);
        request2 = new ItemRequest(2L, "запрос2", booker, created);
        item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        item2 = new Item(2L, "item2", "description2", Status.UNAVAILABLE, owner, request2);
        booking1 = new Booking(1L, start, end, item1, booker, ru.practicum.shareit.booking.Status.WAITING);
        booking2 = new Booking(2L, start, end, item2, booker, ru.practicum.shareit.booking.Status.WAITING);
        bookingIncomeDto = new BookingIncomeDto(1L, start, end, 1L);
        bookingOutcomeDto = new BookingOutcomeDto(1L, start, end, item1, booker, booking1.getStatus().name());
        bookingOutcomeDto2 = new BookingOutcomeDto(1L, start, end, item2, booker, booking2.getStatus().name());
    }

    @Test
    @DisplayName("При запросе сохранение бронирования вернуть BookingOutcomeDto")
    void saveNewBooking_shouldReturnBookingOutcomeDto() {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        Mockito.when(bookingService.saveNewBooking(any(), any(), anyLong(), anyLong())).thenReturn(booking1);

        bookingController.saveNewBooking(userId, new BookingIncomeDto(bookingId, start, end, itemId));

        verify(bookingService).saveNewBooking(start, end, 1L, 1L);
    }

    @Test
    @DisplayName("При запросе подтверждение бронирования вернуть BookingOutcomeDto")
    void approveBooking_shouldReturnBookingOutcomeDto() {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingOutcomeDto testDto = new BookingOutcomeDto(bookingId, start, end, item1, booker, booking1.getStatus().name());
        Mockito.when(bookingService.updateBooking(anyLong(), anyLong(), any())).thenReturn(booking1);

        BookingOutcomeDto result = bookingController.approveBooking(bookingId, userId, true);

        Assertions.assertAll(
                () -> assertEquals(testDto.getId(), result.getId()),
                () -> assertEquals(testDto.getStart(), result.getStart()),
                () -> assertEquals(testDto.getEnd(), result.getEnd()),
                () -> assertEquals(testDto.getItem().getId(), result.getItem().getId()),
                () -> assertEquals(testDto.getStatus(), result.getStatus())
        );
    }

    @Test
    @DisplayName("При запросе показать бронирование вернуть BookingOutcomeDto")
    void getBookingById_shouldReturnBookingOutcomeDto() {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingOutcomeDto testDto = new BookingOutcomeDto(bookingId, start, end, item1, booker, booking1.getStatus().name());

        Mockito.when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(booking1);

        BookingOutcomeDto result = bookingController.getBookingById(userId, bookingId);

        Assertions.assertAll(
                () -> assertEquals(testDto.getId(), result.getId()),
                () -> assertEquals(testDto.getStart(), result.getStart()),
                () -> assertEquals(testDto.getEnd(), result.getEnd()),
                () -> assertEquals(testDto.getItem().getId(), result.getItem().getId()),
                () -> assertEquals(testDto.getStatus(), result.getStatus())
        );
    }

    @Test
    @DisplayName("При запросе вернуть все бронирования пользователя - список BookingOutcomeDto")
    void getBookingsByUser_shouldReturnBookingOutcomeDtos() {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        int from = 0;
        int size = 10;
        SearchStatus status;
        List<Booking> bookings = List.of(booking1, booking2);
        List<BookingOutcomeDto> bookingOutcomeDtos = bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        Mockito.when(bookingService.getBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookings);

        List<BookingOutcomeDto> result = bookingController.getBookingsByUser(userId, "ALL", from / size, size);

        Assertions.assertTrue(bookingOutcomeDtos.size() == result.size() && bookingOutcomeDtos.containsAll(result) && result.containsAll(bookingOutcomeDtos));
    }

    @Test
    void getBookingsByOwner() {
    }
}