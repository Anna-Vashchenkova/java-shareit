package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    public static final long USER_2_ID = 2L;
    public static final long ITEM_ID = 1L;
    @InjectMocks
    CommentServiceImpl commentService;
    @Mock
    private CommentRepository repository;
    @Mock
    private BookingService bookingService;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");
    private LocalDateTime created = LocalDateTime.of(2024, 02, 29, 12, 0, 0);
    private LocalDateTime created2 = LocalDateTime.of(2024, 3, 1, 14, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", validUser2, created);
    private Item item1 = new Item(1L, "перфоратор", "vvv", Status.AVAILABLE, validUser1, request1);
    private Comment comment1 = new Comment(1L, "super", item1, validUser2, created2);
    private Booking booking1 = new Booking(1L,
            LocalDateTime.of(2024, 3, 1, 12, 0, 0),
            LocalDateTime.of(2024, 3, 1, 13, 0, 0),
            item1,
            validUser2,
            ru.practicum.shareit.booking.Status.APPROVED);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", validUser2, created);
    private Item item2 = new Item(2L, "перфоратор2", "vvv2", Status.AVAILABLE, validUser1, request2);

    @Test
    @DisplayName("Получить список комментариев в итему")
    void getComments_shouldReturnComments() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        Mockito.when(repository.findAllByItemId(1L)).thenReturn(comments);

        List<Comment> result = commentService.getComments(1L);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findAllByItemId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        Mockito.when(repository.findAllByItemId(1L)).thenReturn(comments);

        List<Comment> result = commentService.findAllByItemId(1L);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Успешное добавление комментария- при валидных параметрах метода вернуть Comment")
    void addComment_whenParamIsValid_thenReturnComment() {
        String text = "super";
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        Mockito.when(userService.getUserById(USER_2_ID)).thenReturn(validUser2);
        Mockito.when(itemService.getItemById(USER_2_ID, ITEM_ID)).thenReturn(item1);
        Mockito.when(bookingService.getBookings(USER_2_ID, SearchStatus.PAST, 0, 10)).thenReturn(bookings);

        commentService.addComment(USER_2_ID, ITEM_ID, text);

        verify(repository).save(commentCaptor.capture());
        assertAll(
                () -> assertEquals(item1, commentCaptor.getValue().getItem()),
                () -> assertEquals(validUser2, commentCaptor.getValue().getAuthor()),
                () -> assertEquals(text, commentCaptor.getValue().getText()),
                () -> assertNull(commentCaptor.getValue().getId())
        );
    }

    @Test
    @DisplayName("Вернуть ошибку при значении пользователя null")
    void addComment_whenParamIsNotValid_thenReturnDataNotFoundException() {
        String text = "super";
        Long userId = 10L;
        Long itemId = 1L;
        Mockito.when(userService.getUserById(userId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> commentService.addComment(userId, itemId, text));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Вернуть ошибку при значении параметра text null")
    void addComment_whenParamTextIsNull_thenReturnDataNotFoundException() {
        Long userId = 2L;
        Long itemId = 1L;

        Mockito.when(userService.getUserById(userId)).thenReturn(validUser2);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> commentService.addComment(userId, itemId, null));

        Assertions.assertEquals("Текст комментария не может быть пустым", exception.getMessage());
    }

    @Test
    @DisplayName("Вернуть ошибку при значении параметра item null")
    void addComment_whenParamItemIsNull_thenReturnDataNotFoundException() {
        String text = "super";
        Long userId = 2L;
        Long itemId = 10L;

        Mockito.when(userService.getUserById(userId)).thenReturn(validUser2);
        Mockito.when(itemService.getItemById(userId, itemId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> commentService.addComment(userId, itemId, text));

        Assertions.assertEquals("Вещь с таким id не найдена", exception.getMessage());
    }
}