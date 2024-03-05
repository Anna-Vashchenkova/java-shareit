package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerMvcTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
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
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusDays(10);
        request1 = new ItemRequest(1L, "request1", booker, created);
        request2 = new ItemRequest(2L, "request2", booker, created);
        item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        item2 = new Item(2L, "item2", "description2", Status.UNAVAILABLE, owner, request2);
        booking1 = new Booking(1L, start, end, item1, booker, ru.practicum.shareit.booking.Status.WAITING);
        booking2 = new Booking(2L, start, end, item2, booker, ru.practicum.shareit.booking.Status.WAITING);
        bookingIncomeDto = new BookingIncomeDto(1L, start, end, 1L);
        bookingOutcomeDto = new BookingOutcomeDto(1L, start, end, item1, booker, booking1.getStatus().name());
        bookingOutcomeDto2 = new BookingOutcomeDto(2L, start, end, item2, booker, booking2.getStatus().name());
    }

    @Test
    @DisplayName("При запросе должен вернуться BookingOutcomeDto")
    void saveNewBooking_shouldReturnBookingOutcomeDto() throws Exception {
        Mockito.when(bookingService.saveNewBooking(start, end, 1L, 1L)).thenReturn(booking1);

        String result = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingIncomeDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingOutcomeDto), result);
    }

    @Test
    @DisplayName("При запросе на подтверждение должен вернуться BookingOutcomeDto")
    void approveBooking() throws Exception {
        Mockito.when(bookingService.updateBooking(1L,1L,true)).thenReturn(booking1);
        bookingOutcomeDto.setStatus(ru.practicum.shareit.booking.Status.APPROVED.name());
        String result = mvc.perform(patch("/bookings/1", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved","true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingOutcomeDto), result);
    }

    @Test
    @DisplayName("При запросе должен вернуться BookingOutcomeDto")
    void getBookingById_whenUserIsFound_thenReturnBookingOutcomeDto() throws Exception {
        Mockito.when(bookingService.getBookingById(1L,1L)).thenReturn(booking1);

        String result = mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingOutcomeDto), result);
    }

    @Test
    @DisplayName("Получение списка бронирований по userId")
    void getBookingsByUser() throws Exception {
        List<Booking> bookings = List.of(booking1, booking2);
        Mockito.when(bookingService.getBookings(1L, SearchStatus.ALL, 0, 10)).thenReturn(bookings);
        List<BookingOutcomeDto> dtoList = bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

        String result = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state","ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    @DisplayName("Получение списка бронирований по ownerId")
    void getBookingsByOwner() throws Exception {
        List<BookingOutcomeDto> dtoList = List.of(bookingOutcomeDto, bookingOutcomeDto2);
        List<Booking> bookings = List.of(booking1, booking2);
        Mockito.when(bookingService.getBookingsByOwner(1L, SearchStatus.ALL, 0, 10)).thenReturn(bookings);

        String result = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state","ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }
}