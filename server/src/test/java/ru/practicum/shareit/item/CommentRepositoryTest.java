package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class CommentRepositoryTest {
    private final TestEntityManager entityManager;
    private final CommentRepository repository;

    private User booker;
    private User owner;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .name("booker")
                .email("user1@mail.ru")
                .build();
        owner = User.builder()
                .name("owner")
                .email("user2@mail.ru")
                .build();
        created = LocalDateTime.now();
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusDays(10);
        request1 = ItemRequest.builder()
                .description("request1")
                .requestor(booker)
                .createdTime(created)
                .build();
        request2 = ItemRequest.builder()
                .description("request2")
                .createdTime(created)
                .requestor(booker)
                .build();
        item1 = Item.builder()
                .name("item1")
                .description("description1")
                .available(Status.AVAILABLE)
                .owner(owner)
                .request(request1)
                .build();
        item2 = Item.builder()
                .name("item2")
                .description("description2")
                .available(Status.UNAVAILABLE)
                .owner(owner)
                .request(request2)
                .build();
        booking1 = Booking.builder()
                .start(start)
                .end(end)
                .item(item1)
                .booker(booker)
                .status(ru.practicum.shareit.booking.Status.WAITING)
                .build();
        booking2 = Booking.builder()
                .start(start)
                .end(end)
                .item(item2)
                .booker(booker)
                .status(ru.practicum.shareit.booking.Status.WAITING)
                .build();
        comment1 = Comment.builder()
                .text("comment1 text")
                .item(item1)
                .author(booker)
                .created(created)
                .build();
        comment2 = Comment.builder()
                .text("comment2 text")
                .item(item2)
                .author(booker)
                .created(created)
                .build();
    }

    @Test
    void findAllByItemId() {
        comment2.setItem(item1);
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(comment1);
        entityManager.persist(comment2);

        List<Comment> comments = List.of(comment1, comment2);

        List<Comment> result = repository.findAllByItemId(item1.getId());

        Assertions.assertTrue(comments.size() == result.size() && comments.containsAll(result) && result.containsAll(comments));
    }
}