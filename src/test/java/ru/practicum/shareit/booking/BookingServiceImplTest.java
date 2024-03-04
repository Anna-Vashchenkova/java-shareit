package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    private BookingRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Captor
    ArgumentCaptor<Booking> bookingCaptor;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.now().minusDays(1L);
    private LocalDateTime created2 = LocalDateTime.now().plusDays(7L);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", validUser2, created);
    private Item item1 = new Item(1L, "перфоратор", "vvv", ru.practicum.shareit.item.model.Status.AVAILABLE, validUser1, request1);
    private Comment comment1 = new Comment(1L, "super", item1, validUser2, created2);
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = LocalDateTime.now().plusHours(6L);
    private Booking booking1 = new Booking(1L, start, end, item1, validUser2, Status.WAITING);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", validUser2, created);
    private Item item2 = new Item(2L, "перфоратор2", "vvv2", ru.practicum.shareit.item.model.Status.AVAILABLE, validUser1, request2);

    @Test
    @DisplayName("Успешное добавление бронирования")
    void saveNewBooking_whenParamIsValid_thenReturnBooking() {
        Long userId = 2L;
        Long itemId = 1L;
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser2);
        Mockito.when(itemService.getItemById(userId, itemId)).thenReturn(item1);
        Mockito.when(repository.save(any())).thenReturn(booking1);

        Booking result = bookingService.saveNewBooking(start, end, itemId, userId);

        verify(repository).save(any());
        Assertions.assertEquals(booking1, result);
    }

    @Test
    @DisplayName("При значении пользователя null вернуть DataNotFoundException")
    void saveNewBooking_whenParamIsNotValid_thenReturnDataNotFoundException() {
        Long userId = 10L;
        Long itemId = 1L;
        Mockito.when(userService.getUserById(userId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> bookingService.saveNewBooking(start, end, itemId, userId));

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("При значении UserId = BookerId вернуть DataNotFoundException")
    void saveNewBooking_whenUserIdEqualsBookerId_thenReturnDataNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(itemService.getItemById(userId, itemId)).thenReturn(item1);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> bookingService.saveNewBooking(start, end, itemId, userId));
        Assertions.assertEquals("Вещь не может быть забронирована её владельцем.", exception.getMessage());
    }

    @Test
    @DisplayName("При значении поля начала бронирования позже окончания вернуть ValidationException")
    void saveNewBooking_whenStartIsAfterend_thenReturnValidationException() {
        Long userId = 2L;
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now().plusHours(6);
        LocalDateTime end = LocalDateTime.now();
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser2);
        Mockito.when(itemService.getItemById(userId, itemId)).thenReturn(item1);

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.saveNewBooking(start, end, itemId, userId));
        Assertions.assertEquals("Время начала бронирования не может быть позже окончания.", exception.getMessage());
    }

    @Test
    @DisplayName("При валидных параметрах вернуть Booking")
    void updateBooking_whenParamIsValid_thenReturnBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(itemService.getAllItems(userId)).thenReturn(items);
        Mockito.when(repository.findById(any())).thenReturn(Optional.of(booking1));
        Mockito.when(repository.save(any())).thenReturn(booking1);

        Booking result = bookingService.updateBooking(bookingId, userId, approved);

        verify(repository).save(any());
        Assertions.assertEquals(booking1, result);
    }

    @Test
    @DisplayName("При невалидных параметрах вернуть DataNotFoundException")
    void updateBooking_whenUserIsNotFound_thenReturnDataNotFoundException() {
        Long userId = 10L;
        Long bookingId = 1L;
        Boolean approved = true;
        Mockito.when(userService.getUserById(userId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> bookingService.updateBooking(bookingId, userId, approved));
        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("При обновлении бронирования время которого истекло вернуть ValidationException")
    void updateBooking_whenEndIsBefore_thenReturnValidationException() {
        LocalDateTime startTest = LocalDateTime.now().minusDays(1);
        LocalDateTime endTest = LocalDateTime.now().minusHours(3);
        User userTest = new User(3L, "test@mail.ru", "testUser");
        Item itemTest = new Item(3L, "itemTest", "descriptionTest", ru.practicum.shareit.item.model.Status.AVAILABLE, userTest, request1);
        Booking oldBooking = new Booking(1L, startTest, endTest, itemTest, validUser2, Status.WAITING);
        Mockito.when(userService.getUserById(anyLong())).thenReturn(validUser2);
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(oldBooking.getId(), userTest.getId(), true));

        assertEquals("Время бронирования уже истекло!", exception.getMessage());
    }

    @Test
    @DisplayName("При обновлении бронирования не владельцем итема вернуть DataNotFoundException")
    void updateBooking_whenEndIsBefore_thenReturnDataNotFoundException() {
        LocalDateTime startTest = LocalDateTime.now();
        LocalDateTime endTest = LocalDateTime.now().plusHours(3);
        User userTest = new User(3L, "test@mail.ru", "testUser");
        Item itemTest = new Item(3L, "itemTest", "descriptionTest", ru.practicum.shareit.item.model.Status.AVAILABLE, userTest, request1);
        Booking oldBooking = new Booking(1L, startTest, endTest, itemTest, validUser2, Status.WAITING);
        when(userService.getUserById(anyLong())).thenReturn(validUser2);
        when(repository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.updateBooking(oldBooking.getId(), validUser2.getId(), true));

        assertEquals("Подтвердить бронирование может только владелец вещи!", exception.getMessage());
    }

    @Test
    @DisplayName("При обновлении отклоненного бронирования владельцем итема вернуть ValidationException")
    void updateBooking_whenStatusIsCANCELED_thenReturnValidationException() {
        LocalDateTime startTest = LocalDateTime.now();
        LocalDateTime endTest = LocalDateTime.now().plusHours(3);
        Item itemTest = new Item(3L, "itemTest", "descriptionTest", ru.practicum.shareit.item.model.Status.AVAILABLE, validUser1, request1);
        List<Item> items = List.of(itemTest);
        Booking oldBooking = new Booking(1L, startTest, endTest, itemTest, validUser2, Status.WAITING);
        Mockito.when(userService.getUserById(anyLong())).thenReturn(validUser2);
        Mockito.when(itemService.getAllItems(any())).thenReturn(items);
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        Booking result = bookingService.updateBooking(oldBooking.getId(), validUser2.getId(), false);
        verify(repository).save(bookingCaptor.capture());
        Assertions.assertEquals(Status.CANCELED, bookingCaptor.getValue().getStatus());
   }

    @Test
    @DisplayName("При вылидных параметрах вернуть Booking")
    void getBookingById_whenParamIsValid_thenReturnBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        Long bookingId = 1L;
        List<Item> items = new ArrayList<>();
        items.add(item1);
        boolean isItemOwner = true;
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(repository.findById(itemId)).thenReturn(Optional.of(booking1));
        Mockito.when(itemService.findItemsByOwnerId(userId)).thenReturn(items);

        Booking result = bookingService.getBookingById(userId, bookingId);

        Assertions.assertEquals(booking1, result);
    }

    @Test
    @DisplayName("При невалидных параметрах вернуть DataNotFoundException")
    void getBookingById_whenUserIsNotFound_thenReturnDataNotFoundException() {
        Long userId = 10L;
        Long itemId = 1L;
        Long bookingId = 1L;
        List<Item> items = new ArrayList<>();
        items.add(item1);
        boolean isItemOwner = true;
        Mockito.when(userService.getUserById(userId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.getBookingById(userId, bookingId));

        assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("При невылидных параметрах вернуть DataNotFoundException")
    void getBookingById_whenItemIsNotFound_thenReturnDataNotFoundException() {
        Long userId = 1L;
        Long itemId = 10L;
        Long bookingId = 1L;
        List<Item> items = new ArrayList<>();
        items.add(item1);
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);

        DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.getBookingById(userId, bookingId));

        assertEquals("Вещь с таким id не найдена.", exception.getMessage());
    }

    @Test
    @DisplayName("Получение бронирования по пользователю CURRENT")
    void getBookings_whenUserFoundAnsStatusCURRENT_thenReturnBookings() {
        Long userId = 2L;
        List<Booking> bookings = List.of(booking1);
        Mockito.when(userService.getUserById(anyLong())).thenReturn(validUser2);
        Mockito.when(repository.getBookingForBookerAndStartIsBeforeAndEndAfter(
                anyLong(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookings(userId, SearchStatus.CURRENT, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю PAST")
    void getBookings_whenUser_PAST_thenReturnBookings() {
        Long userId = 2L;
        List<Booking> bookings = List.of(booking1);
        when(userService.getUserById(anyLong())).thenReturn(validUser2);
        when(repository.getBookingForBookerAndEndBefore(anyLong(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookings(userId, SearchStatus.PAST, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю FUTURE")
    void getBookings_whenUser_FUTURE_thenReturnBookings() {
        Long userId = 1L;
        List<Booking> bookings = List.of(booking1);
        when(userService.getUserById(anyLong())).thenReturn(validUser2);
        when(repository.getBookingForBookerIdAndStartAfter(anyLong(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookings(userId, SearchStatus.FUTURE, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю WAITING")
    void getBookings_whenUser_WAITING_thenReturnBookings() {
        Long userId = 1L;
        List<Booking> bookings = List.of(booking1);
        when(userService.getUserById(anyLong())).thenReturn(validUser2);
        when(repository.getBookingForBookerAndStatus(anyLong(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookings(userId, SearchStatus.WAITING, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю REJECTED")
    void getBookings_whenUser_REJECTED_thenReturnBookings() {
        Long userId = 1L;
        List<Booking> bookings = List.of(booking1);
        when(userService.getUserById(anyLong())).thenReturn(validUser2);
        when(repository.getBookingForBookerAndStatus(anyLong(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookings(userId, SearchStatus.REJECTED, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение всех бронирований по пользователю")
    void getBookings_whenUserIsFound_thenReturnBookings() {
        Long userId = 1L;
        List<Booking> bookings = List.of(booking1);
        Page mockPage = Mockito.mock(Page.class);
        Mockito.when(mockPage.getContent()).thenReturn(bookings);
        when(userService.getUserById(anyLong())).thenReturn(validUser2);
        when(repository.findAllByBookerId(anyLong(), any())).thenReturn(mockPage);

        List<Booking> result = bookingService.getBookings(userId, SearchStatus.ALL, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("При значении пользователя null вернуть ошибку")
    public void getBookings_whenUserIsNotFound_thenReturnDataNotFoundException() {
        Long userId = 10L;
        Mockito.when(userService.getUserById(userId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> bookingService.getBookings(userId, SearchStatus.ALL, 0, 10));

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Получение бронирования по пользователю-владельцу CURRENT")
    void getBookingsByOwner_whenUserFoundAnsStatusCURRENT_thenReturnBookings() {
        Long userId = 1L;
        List<Booking> bookings = List.of(booking1);
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(repository.getBookingByOwner_IdAndStartIsBeforeAndEndAfter(any(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByOwner(userId, SearchStatus.CURRENT, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю-владельцу PAST")
    void getBookingsByOwner_whenUserFoundAnsStatusPAST_thenReturnBookings() {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1L);
        LocalDateTime end = LocalDateTime.now().minusHours(6L);
        Booking bookingPast = new Booking(1L, start, end, item1, validUser2, Status.APPROVED);
        List<Booking> bookings = List.of(bookingPast);
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(repository.getBookingByOwner_IdAndEndBefore(any(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByOwner(userId, SearchStatus.PAST, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю-владельцу - статус FUTURE")
    void getBookingsByOwner_whenUserFoundAnsStatusFUTURE_thenReturnBookings() {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now().plusHours(6L);
        LocalDateTime end = LocalDateTime.now().plusDays(1L);
        Booking bookingF = new Booking(1L, start, end, item1, validUser2, Status.WAITING);
        List<Booking> bookings = List.of(bookingF);
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(repository.getBookingByOwnerIdAndStartAfter(any(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByOwner(userId, SearchStatus.FUTURE, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю-владельцу- статус WAITING")
    void getBookingsByOwner_whenUserFoundAnsStatusWAITING_thenReturnBookings() {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1L);
        Booking booking = new Booking(1L, start, end, item1, validUser2, Status.WAITING);
        List<Booking> bookings = List.of(booking);
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(repository.getBookingByOwner_IdAndStatus(any(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByOwner(userId, SearchStatus.WAITING, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю-владельцу -статус REJECTED")
    void getBookingsByOwner_whenUserFoundAnsStatusREJECTED_thenReturnBookings() {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1L);
        Booking booking = new Booking(1L, start, end, item1, validUser2, Status.REJECTED);
        List<Booking> bookings = List.of(booking);
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(repository.getBookingByOwner_IdAndStatus(any(), any())).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByOwner(userId, SearchStatus.REJECTED, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("Получение всех бронирований по пользователю-владельцу -статус ALL")
    void getBookingsByOwner_whenUserFoundAnsStatusALL_thenReturnBookings() {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1L);
        Booking booking = new Booking(1L, start, end, item1, validUser2, Status.WAITING);
        List<Booking> bookings = List.of(booking);
        Page mockPage = Mockito.mock(Page.class);
        Mockito.when(mockPage.getContent()).thenReturn(bookings);
        Mockito.when(userService.getUserById(userId)).thenReturn(validUser1);
        Mockito.when(repository.findAllByOwnerId(any(), any())).thenReturn(mockPage);

        List<Booking> result = bookingService.getBookingsByOwner(userId, SearchStatus.ALL, 0, 10);

        assertEquals(bookings, result);
    }

    @Test
    @DisplayName("При значении пользователя null вернуть ошибку")
    void getBookingsByOwner_whenUserIsNotFound_thenReturnDataNotFoundException() {
        Long userId = 10L;
        Mockito.when(userService.getUserById(userId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> bookingService.getBookingsByOwner(userId, SearchStatus.ALL, 0, 10));

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("При запросе получить список бронирований по id итема")
    void getBookingsForUser_shouldReturnBookings() {
        Long itemId = 1L;
        List<Booking> bookings = List.of(booking1);
        Mockito.when(repository.findAllByItemId(itemId)).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsForUser(itemId);
        assertEquals(bookings, result);
    }
}