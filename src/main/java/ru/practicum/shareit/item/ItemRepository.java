package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item as i " +
            "where upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%'))")
    List<Item> searchItem(String text);

    @Query("delete from Item as i " +
            "where i.id = :id and i.owner.id = :userId")
    void deleteByUserIdAndItemId(Long userId, Long id);

    @Query("select i from Item as i " +
            "where i.owner.id = :userId")
    List<Item> findByUserId(Long userId);
}
