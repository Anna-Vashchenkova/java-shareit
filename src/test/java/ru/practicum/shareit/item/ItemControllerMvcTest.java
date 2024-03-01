package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerMvcTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    ItemService itemService;
    @MockBean
    BookingService bookingService;
    @MockBean
    CommentService commentService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.of(2024, 02, 29, 12, 0, 0);
    private LocalDateTime created2 = LocalDateTime.of(2024, 3, 1, 14, 0, 0);
    private LocalDateTime created3 = LocalDateTime.of(2024, 3, 1, 20, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", validUser1, created);
    Item item3 = new Item(3L, "дрель", "ddd", Status.AVAILABLE, validUser2, request1);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", validUser1, created);
    Item item4 = new Item(4L, "дрель2", "ddd2", Status.AVAILABLE, validUser2, request2);
    private ItemRequest request3 = new ItemRequest(3L, "запрос3", validUser2, created);
    Item item1 = new Item(1L, "перфоратор", "vvv", Status.AVAILABLE, validUser1, request3);
    private ItemRequest request4 = new ItemRequest(4L, "запрос4", validUser2, created);
    Item item2 = new Item(2L, "перфоратор2", "vvv2", Status.AVAILABLE, validUser1, request4);

    @Test
    @DisplayName("При запросе должен вернуться список ItemOutcomeInfoDto")
    void get_shouldReturnAllItemsByUser() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito.when(this.itemService.getItems(1L, 0, 10)).thenReturn(items);

        mvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("При запросе должен вернуться ItemOutcomeDto")
    void add_shouldReturnItemOutcomeDto() throws Exception {
        Mockito
                .when(this.itemService.addNewItem(1L, "перфоратор", "vvv", true, 3L))
                .thenReturn(item1);

        mvc.perform(
                post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\n" +
                                "  \"name\": \"перфоратор\",\n" +
                                "  \"description\": \"vvv\",\n" +
                                "  \"available\": true,\n" +
                                "  \"requestId\": 3\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("перфоратор"))
                .andExpect(jsonPath("$.description").value("vvv"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.owner.id").value(1))
                .andExpect(jsonPath("$.owner.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.owner.name").value("Aa"))
                .andExpect(jsonPath("$.requestId").value(3));

    }

    @Test
    @DisplayName("При запросе несуществующего пользователя должна появиться ошибка")
    void updateItem_whenUserNotFound_thenReturnValidationException() throws Exception {
        Mockito.when(this.itemService.updateItem(any(), any(), anyString(), anyString(), any()))
                .thenThrow(new ValidationException(""));

        mvc.perform(patch("/items/10")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"перфоратор\",\n" +
                                "  \"description\": \"vvv\",\n" +
                                "  \"available\": true,\n" +
                                "  \"requestId\": 3\n" +
                                "}"))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("При запросе должен вернуться ItemOutcomeInfoDto")
    void getItemById_shouldReturnItemOutcomeInfoDto() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking(1L,
                LocalDateTime.of(2024, 3, 1, 12, 0, 0),
                LocalDateTime.of(2024, 3, 1, 13, 0, 0),
                item1,
                validUser2,
                ru.practicum.shareit.booking.Status.APPROVED
                );
        Booking booking2 = new Booking(2L,
                LocalDateTime.of(2024, 3, 1, 18, 0, 0),
                LocalDateTime.of(2024, 3, 1, 19, 0, 0),
                item1,
                validUser2,
                ru.practicum.shareit.booking.Status.APPROVED
        );
        bookings.add(booking1);
        bookings.add(booking2);
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment(1L, "super", item1, validUser2, created2);
        Comment comment2 = new Comment(2L, "super2", item1, validUser2, created3);
        comments.add(comment1);
        comments.add(comment2);

        Mockito.when(this.bookingService.getBookingsForUser(1L)).thenReturn(bookings);
        Mockito.when(this.commentService.getComments(1L)).thenReturn(comments);
        Mockito.when(this.itemService.getItemById(1L, 1L)).thenReturn(item1);

        mvc.perform(
                get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("перфоратор"))
                .andExpect(jsonPath("$.description").value("vvv"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.owner.id").value(1))
                .andExpect(jsonPath("$.owner.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.owner.name").value("Aa"))
                .andExpect(jsonPath("$.requestId").value(3))
                .andExpect(jsonPath("$.lastBooking.id").value(1))
                .andExpect(jsonPath("$.lastBooking.bookerId").value(2))
                .andExpect(jsonPath("$.lastBooking.start").value("2024-03-01T12:00:00"))
                .andExpect(jsonPath("$.lastBooking.end").value("2024-03-01T13:00:00"))
                .andExpect(jsonPath("$.nextBooking.id").value(2))
                .andExpect(jsonPath("$.nextBooking.bookerId").value(2))
                .andExpect(jsonPath("$.nextBooking.start").value("2024-03-01T18:00:00"))
                .andExpect(jsonPath("$.nextBooking.end").value("2024-03-01T19:00:00"))
                .andExpect(jsonPath("$.comments.[0].id").value(1))
                .andExpect(jsonPath("$.comments.[0].text").value("super"))
                .andExpect(jsonPath("$.comments.[0].authorName").value("Bb"))
                .andExpect(jsonPath("$.comments.[0].created").value("2024-03-01T14:00:00"))
                .andExpect(jsonPath("$.comments.[1].id").value(2))
                .andExpect(jsonPath("$.comments.[1].text").value("super2"))
                .andExpect(jsonPath("$.comments.[1].authorName").value("Bb"))
                .andExpect(jsonPath("$.comments.[1].created").value("2024-03-01T20:00:00"));
    }

    @Test
    @DisplayName("При запросе ответ 200")
    void deleteItem() throws Exception{
        mvc.perform(
                        delete("/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItem() {
    }

    @Test
    void addComment() {
    }
}