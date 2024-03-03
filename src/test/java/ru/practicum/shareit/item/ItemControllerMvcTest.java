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

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerMvcTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private CommentService commentService;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.of(2024, 02, 29, 12, 0, 0);
    private LocalDateTime created2 = LocalDateTime.of(2024, 3, 1, 14, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос3", validUser2, created);
    private Item item1 = new Item(1L, "перфоратор", "vvv", Status.AVAILABLE, validUser1, request1);
    private ItemRequest request2 = new ItemRequest(2L, "запрос4", validUser2, created);
    private Item item2 = new Item(2L, "перфоратор2", "vvv2", Status.AVAILABLE, validUser1, request2);
    private Booking booking1 = new Booking(1L,
            LocalDateTime.of(2024, 3, 1, 12, 0, 0),
            LocalDateTime.of(2024, 3, 1, 13, 0, 0),
            item1,
            validUser2,
            ru.practicum.shareit.booking.Status.APPROVED);
    private Booking booking2 = new Booking(2L,
            LocalDateTime.of(2024, 3, 30, 18, 0, 0),
            LocalDateTime.of(2024, 3, 30, 19, 0, 0),
            item1,
            validUser2,
            ru.practicum.shareit.booking.Status.APPROVED);

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
                .when(this.itemService.addNewItem(1L, "перфоратор", "vvv", true, 1L))
                .thenReturn(item1);

        mvc.perform(
                post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content("{\n" +
                                "  \"name\": \"перфоратор\",\n" +
                                "  \"description\": \"vvv\",\n" +
                                "  \"available\": true,\n" +
                                "  \"requestId\": 1\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("перфоратор"))
                .andExpect(jsonPath("$.description").value("vvv"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.owner.id").value(1))
                .andExpect(jsonPath("$.owner.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.owner.name").value("Aa"))
                .andExpect(jsonPath("$.requestId").value(1));

    }

    @Test
    @DisplayName("При запросе существующего пользователя должен вернуться ItemOutcomeDto")
    void updateItem_whenUserFound_thenReturnItemOutcomeDto() throws Exception {
        Mockito.when(this.itemService.updateItem(any(), any(), anyString(), anyString(), any())).thenReturn(item1);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"перфоратор\",\n" +
                                "  \"description\": \"vvv\",\n" +
                                "  \"available\": true,\n" +
                                "  \"requestId\": 1\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("перфоратор"))
                .andExpect(jsonPath("$.description").value("vvv"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.owner.id").value(1))
                .andExpect(jsonPath("$.owner.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.owner.name").value("Aa"))
                .andExpect(jsonPath("$.requestId").value(1));
    }

    @Test
    @DisplayName("При запросе несуществующего пользователя должна появиться ошибка")
    void updateItem_whenUserNotFound_thenReturnValidationException() throws Exception {
        Mockito.when(this.itemService.updateItem(any(), any(), anyString(), anyString(), any()))
                .thenThrow(new ValidationException(""));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"name\": \"перфоратор\",\n" +
                                "  \"description\": \"vvv\",\n" +
                                "  \"available\": true,\n" +
                                "  \"requestId\": 1\n" +
                                "}"))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("При запросе должен вернуться ItemOutcomeInfoDto")
    void getItemById_shouldReturnItemOutcomeInfoDto() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment(1L, "super", item1, validUser2, created2);
        comments.add(comment1);

        Mockito.when(this.itemService.userIsOwnerOfItem(1L, 1L)).thenReturn(true);
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
                .andExpect(jsonPath("$.requestId").value(1))
                .andExpect(jsonPath("$.lastBooking.id").value(1))
                .andExpect(jsonPath("$.lastBooking.bookerId").value(2))
                .andExpect(jsonPath("$.lastBooking.start").value("2024-03-01T12:00:00"))
                .andExpect(jsonPath("$.lastBooking.end").value("2024-03-01T13:00:00"))
                .andExpect(jsonPath("$.nextBooking.id").value(2))
                .andExpect(jsonPath("$.nextBooking.bookerId").value(2))
                .andExpect(jsonPath("$.nextBooking.start").value("2024-03-30T18:00:00"))
                .andExpect(jsonPath("$.nextBooking.end").value("2024-03-30T19:00:00"))
                .andExpect(jsonPath("$.comments.[0].id").value(1))
                .andExpect(jsonPath("$.comments.[0].text").value("super"))
                .andExpect(jsonPath("$.comments.[0].authorName").value("Bb"))
                .andExpect(jsonPath("$.comments.[0].created").value("2024-03-01T14:00:00"));
    }

    @Test
    @DisplayName("При запросе ответ 200")
    void deleteItem() throws Exception {
        mvc.perform(
                        delete("/items/1")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("При запросе с валидными параметрами возвращается список ItemOutcomeDto")
    void searchItem_whenRequestParamIsValid_thenReturnItemOutcomeDto() throws Exception {
        int from = 0;
        int size = 10;
        String text = "перфоратор";
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Mockito.when(this.itemService.searchItem(text, from / size, size)).thenReturn(items);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "перфоратор")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("перфоратор"))
                .andExpect(jsonPath("$.[0].description").value("vvv"))
                .andExpect(jsonPath("$.[0].available").value("true"))
                .andExpect(jsonPath("$.[0].owner.id").value(1))
                .andExpect(jsonPath("$.[0].owner.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.[0].owner.name").value("Aa"))
                .andExpect(jsonPath("$.[0].requestId").value(1))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].name").value("перфоратор2"))
                .andExpect(jsonPath("$.[1].description").value("vvv2"))
                .andExpect(jsonPath("$.[1].available").value("true"))
                .andExpect(jsonPath("$.[1].owner.id").value(1))
                .andExpect(jsonPath("$.[1].owner.email").value("aa@mail.ru"))
                .andExpect(jsonPath("$.[1].owner.name").value("Aa"))
                .andExpect(jsonPath("$.[1].requestId").value(2));
    }

    @Test
    @DisplayName("При запросе с невалидными параметрами from < 0, size < 1 возвращается ошибка")
    void searchItem_whenRequestParamIsNotValid_thenReturnValidationException() throws Exception {
        String text = "перфоратор";
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Mockito.when(this.itemService.searchItem(eq(text), anyInt(), anyInt()))
                .thenThrow(new ValidationException("Неверные параметры запроса"));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "перфоратор")
                        .param("from", "-1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("При запросе возвращается CommentDto")
    void addComment_shouldReturnCommentDto() throws Exception {
        Comment comment1 = new Comment(1L, "super", item1, validUser2, created2);
        Mockito.when(this.commentService.addComment(2L, 1L, "super")).thenReturn(comment1);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"text\": \"super\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("super"))
                .andExpect(jsonPath("$.authorName").value("Bb"))
                .andExpect(jsonPath("$.created").value("2024-03-01T14:00:00"));
    }
}