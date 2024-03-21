package ru.practicum.shareit.request;

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
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestRepositoryTest {
    private final TestEntityManager entityManager;
    private final ItemRequestRepository repository;
    private Pageable pageable =  PageRequest.of(0, 10);
    private User requestor;
    private User owner;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        requestor = User.builder()
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
                .requestor(requestor)
                .createdTime(created)
                .build();
        request2 = ItemRequest.builder()
                .description("request2")
                .createdTime(created)
                .requestor(requestor)
                .build();
    }

    @Test
    @DisplayName("Вернуть список запросов по Id пользователя")
    void findAllByUserId() {
        entityManager.persist(requestor);
        entityManager.persist(request1);
        entityManager.persist(request2);
        List<ItemRequest> requests = List.of(request1, request2);

        List<ItemRequest> result = repository.findAllByUserId(requestor.getId());

        Assertions.assertTrue(requests.size() == result.size() && requests.containsAll(result));
    }

    @Test
    @DisplayName("Вернуть список запросов в виде страницы по Id пользователя")
    void findAll() {
        entityManager.persist(requestor);
        entityManager.persist(owner);
        entityManager.persist(request1);
        entityManager.persist(request2);
        List<ItemRequest> requests = List.of(request1, request2);

        List<ItemRequest> result = repository.findAll(owner.getId(), pageable).getContent();

        Assertions.assertTrue(requests.size() == result.size() && requests.containsAll(result));
    }
}