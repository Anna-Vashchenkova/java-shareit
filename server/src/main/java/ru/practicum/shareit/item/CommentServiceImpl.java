package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public List<Comment> getComments(Long itemId) {
        return repository.findAllByItemId(itemId);
    }

    @Override
    public List<Comment> findAllByItemId(Long itemId) {
        return repository.findAllByItemId(itemId);
    }

    @Override
    public Comment addComment(Long userId, long itemId, String text) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        if ((text == null) || (text.isBlank())) {
            throw new DataNotFoundException("Текст комментария не может быть пустым");
        }
        Item item = itemService.getItemById(userId, itemId);
        if (item == null) {
            throw new DataNotFoundException("Вещь с таким id не найдена");
        }
        bookingService.getBookings(userId, SearchStatus.PAST,0,10)
                .stream()
                .filter(b -> b.getItem().getId() == itemId)
                .findAny().orElseThrow(() -> new ValidationException("Бронь не найдена"));
        return repository.save(new Comment(
                null, text, item, user, LocalDateTime.now()
        ));
    }
}
