package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto dto) {

        return ItemMapper.toItemDto(itemService.addNewItem(
                userId,
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable()
        ));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody ItemDto dto) {
        return ItemMapper.toItemDto(itemService.updateItem(
                userId,
                itemId,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable()
        ));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long itemId) {
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ItemDto searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestParam String text) {
        return ItemMapper.toItemDto(itemService.searchItem(text));
    }
}
