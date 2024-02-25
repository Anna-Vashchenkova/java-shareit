package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select r from ItemRequest as r " +
            "where r.requestor.id = :userId order by r.createdTime desc ")
    List<ItemRequest> findAllByUserId(Long userId);
}
