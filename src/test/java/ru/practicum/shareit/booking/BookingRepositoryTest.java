package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
class BookingRepositoryTest {
    private final TestEntityManager entityManager;
    private final BookingRepository repository;
    private Pageable pageable =  PageRequest.of(0, 10);
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
        bookerDto = new UserDto(1L, "user1@mail.ru", "user1");
        ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
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
                .available(ru.practicum.shareit.item.model.Status.AVAILABLE)
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
    }

    @Test
    @DisplayName("При запросе вернуть список бронирований по владельцу")
    void findAllByOwnerId() {
            entityManager.persist(owner);
            entityManager.persist(booker);
            entityManager.persist(request1);
            entityManager.persist(request2);
            entityManager.persist(item1);
            entityManager.persist(item2);
            entityManager.persist(booking1);
            entityManager.persist(booking2);
            List<Booking> bookingList = List.of(booking1, booking2);

            List<Booking> result = repository.findAllByOwnerId(owner.getId(), pageable).getContent();

            assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список текущих бронирований по владельцу")
    void getBookingByOwner_IdAndStartIsBeforeAndEndAfter() {
            booking1.setStart(LocalDateTime.now().minusHours(5));
            booking2.setStart(LocalDateTime.now().minusHours(10));
            booking1.setEnd(LocalDateTime.now().plusDays(1));
            booking2.setEnd(LocalDateTime.now().plusDays(2));
            entityManager.persist(owner);
            entityManager.persist(booker);
            entityManager.persist(request1);
            entityManager.persist(request2);
            entityManager.persist(item1);
            entityManager.persist(item2);
            entityManager.persist(booking1);
            entityManager.persist(booking2);
            List<Booking> bookingList = List.of(booking1, booking2);

            List<Booking> result = repository.getBookingByOwner_IdAndStartIsBeforeAndEndAfter(owner.getId(), LocalDateTime.now());

            assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список завершенных бронирований по владельцу")
    void getBookingByOwner_IdAndEndBefore() {
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(1));
        booking2.setStart(LocalDateTime.now().minusHours(15));
        booking2.setEnd(LocalDateTime.now().minusHours(10));
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.getBookingByOwner_IdAndEndBefore(owner.getId(), LocalDateTime.now());

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список будущих бронирований по владельцу")
    void getBookingByOwnerIdAndStartAfter() {
        booking1.setStart(LocalDateTime.now().plusHours(12));
        booking2.setStart(LocalDateTime.now().plusHours(10));
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.getBookingByOwnerIdAndStartAfter(owner.getId(), LocalDateTime.now());

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список бронирований по владельцу и статусу")
    void getBookingByOwner_IdAndStatus() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.getBookingByOwner_IdAndStatus(owner.getId(), ru.practicum.shareit.booking.Status.WAITING);

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список бронирований по itemId")
    void findAllByItemId() {
        booking2.setItem(item1);
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.findAllByItemId(item1.getId());

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список текущих бронирований по bookerId")
    void getBookingForBookerAndStartIsBeforeAndEndAfter() {
        booking1.setStart(LocalDateTime.now().minusHours(10));
        booking2.setStart(LocalDateTime.now().minusHours(15));
        booking1.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.getBookingForBookerAndStartIsBeforeAndEndAfter(booker.getId(), LocalDateTime.now());

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список завершенных бронирований по bookerId")
    void getBookingForBookerAndEndBefore() {
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().minusDays(1));
        booking2.setStart(LocalDateTime.now().minusHours(10));
        booking2.setEnd(LocalDateTime.now().minusHours(5));
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.getBookingForBookerAndEndBefore(booker.getId(), LocalDateTime.now());

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список будущих бронирований по bookerId")
    void getBookingForBookerIdAndStartAfter() {
        booking1.setStart(LocalDateTime.now().plusHours(12));
        booking2.setStart(LocalDateTime.now().plusHours(10));
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.getBookingForBookerIdAndStartAfter(booker.getId(), LocalDateTime.now());

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список бронирований по bookerId и статусу")
    void getBookingForBookerAndStatus() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.getBookingForBookerAndStatus(booker.getId(), ru.practicum.shareit.booking.Status.WAITING);

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }

    @Test
    @DisplayName("При запросе вернуть список бронирований по bookerId")
    void findAllByBookerId() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> bookingList = List.of(booking1, booking2);

        List<Booking> result = repository.findAllByBookerId(booker.getId(), pageable).getContent();

        assertTrue(bookingList.size() == result.size() && bookingList.containsAll(result) && result.containsAll(bookingList));
    }
}