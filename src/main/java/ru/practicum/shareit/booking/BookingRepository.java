package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.SearchStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking as b where b.booker.id = :userId and b.status = :state " +
            "order by b.start desc ")
    List<Booking> getBookingByBooker_IdAndStatus(Long userId, SearchStatus state);

    @Query("select b from Booking as b where b.booker.id = :userId order by b.start desc ")
    List<Booking> findAllByOwnerId(Long userId); //ALL

    @Query("select b from Booking as b where b.item.owner.id = :userId and b.start < :data and b.end > :data " +
            "order by b.start desc ")
    List<Booking> getBookingByOwner_IdAndStartIsBeforeAndEndAfter(Long userId, LocalDateTime data); //CURRENT booking

    @Query("select b from Booking as b where b.item.owner.id = :userId and b.end < :data " +
            "order by b.start desc ")
    List<Booking> getBookingByOwner_IdAndEndBefore(Long userId, LocalDateTime data); //PAST booking

    @Query("select b from Booking as b where b.item.owner.id = :userId and b.start > :data " +
            "order by b.start desc ")
    List<Booking> getBookingByOwnerIdAndStartAfter(Long userId, LocalDateTime data); //FUTURE booking добавила проверку на неотмененность

    @Query("select b from Booking as b where b.item.owner.id = :userId and b.status = :status " +
            "order by b.start desc ")
    List<Booking> getBookingByOwner_IdAndStatus(Long userId, Status status); //WAITING ожидающие подтвержд REJECTED отклонённые

    @Query("select b from Booking as b where b.item.id = :itemId ")
    List<Booking> findAllByItemId(Long itemId);
}
