package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;

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
        return items.stream()
                .filter(item1 -> item1.getOwner().getId() == userId)
                .collect(Collectors.toList());
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
                .filter(item -> (item.getId() == itemId) && (item.getOwner().getId() == userId))
                .findFirst();
        if (itemOptional.isPresent()) {
            items.remove(itemOptional.get().getId());
        }
    }

    @Override
    public Item getItemById(long itemId) {
        return items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst().orElse(null);
    }

    @Override
    public List<Item> searchItem(String text) {
        return items.stream()
                .filter(
                        item -> (
                            (item.getName().toLowerCase().contains(text.toLowerCase()))
                            ||
                            (item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        )
                        &&
                        (item.getAvailable() == Status.AVAILABLE)
                )
                .collect(Collectors.toList());
    }

    private Long generateItemId() {
        return lastId ++;
    }
}
