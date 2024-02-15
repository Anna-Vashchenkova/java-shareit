package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.SearchStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking as b where b.booker.id = :userId and b.status = :state " +
            "order by b.start desc ")
    List<Booking> getBookingByBooker_IdAndStatus(Status state, Long userId);

    @Query("select b from Booking as b where b.item.owner.id = :userId and b.status = :state " +
            "order by b.start desc ")
    List<Booking> getBookingByOwner_IdAndStatus(SearchStatus state, Long userId);
}
