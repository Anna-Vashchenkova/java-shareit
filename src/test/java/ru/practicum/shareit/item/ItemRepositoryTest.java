package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRepositoryTest {
    private final TestEntityManager entityManager;
    private final ItemRepository repository;
    private Pageable pageable =  PageRequest.of(0, 10);
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
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusDays(5);
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
                .available(Status.AVAILABLE)
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
    }

    @Test
    @DisplayName("Удаление итема по userId и itemId")
    void deleteByUserIdAndItemId() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> items = List.of(item2);

        repository.save(item1);
        repository.save(item2);
        repository.deleteByUserIdAndItemId(item1.getId(), owner.getId());
        List<Item> result = repository.findByOwnerId(item1.getOwner().getId());

        assertAll(
                ()->assertTrue(items.size() == result.size()),
                ()->assertTrue(items.containsAll(result))
        );
    }

    @Test
    @DisplayName("Вернуть итем при поиске по описанию")
    void searchItem() {
        item2.setDescription(item1.getDescription());
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> items = List.of(item1, item2);

        List<Item> result = repository.searchItem(item1.getDescription(), pageable).getContent();

        Assertions.assertTrue(items.size() == result.size() && items.containsAll(result) && result.containsAll(items));
    }

    @Test
    @DisplayName("Вернуть список всех итемов по Id пользователя")
    void findAllByUserId() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> items = List.of(item1, item2);

        List<Item> result = repository.findAllByUserId(owner.getId());

        Assertions.assertTrue(items.size() == result.size() && items.containsAll(result) && result.containsAll(items));
    }

    @Test
    @DisplayName("Вернуть список всех итемов в виде страниц по Id пользователя")
    void findAllByUserIdPage() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> items = List.of(item1, item2);

        List<Item> result = repository.findAllByUserIdPage(owner.getId(), pageable).getContent();

        Assertions.assertTrue(items.size() == result.size() && items.containsAll(result) && result.containsAll(items));
    }

    @Test
    void findByOwnerId() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> items = List.of(item1, item2);

        List<Item> result = repository.findByOwnerId(owner.getId());

        Assertions.assertTrue(items.size() == result.size() && items.containsAll(result) && result.containsAll(items));
    }

    @Test
    void findAllByRequestId() {
        item2.setRequest(request1);
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);

        List<Item> items = List.of(item1, item2);

        List<Item> result = repository.findAllByRequestId(request1.getId());

        Assertions.assertTrue(items.size() == result.size() && items.containsAll(result) && result.containsAll(items));
    }
}