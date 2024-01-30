package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository{
    private static List<Item> items = new ArrayList<>();
    private Long lastId = 1L;
    @Override
    public List<Item> findByUserId(long userId) {
        return items.stream().filter(item1 -> item1.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(generateItemId());
            items.add(item);
        }
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        Optional<Item> itemOptional = items.stream()
                .filter(item -> (item.getId() == itemId) && (item.getOwner().getId() == userId)
                )
                .findFirst();
        if (itemOptional.isPresent()) {
            items.remove(itemOptional.get().getId());
        }
    }

    private Long generateItemId() {
        return lastId ++;
    }
}
