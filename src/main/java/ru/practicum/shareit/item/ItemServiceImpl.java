package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public List<Item> getItems(Long userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public Item addNewItem(Long userId, Long id, String name, String description, Boolean available) {
        User user = userService.getUserById(userId);
        Status status;
        if (available) {
            status = Status.AVAILABLE;
        } else {
            status = Status.UNAVAILABLE;
        }
        return repository.save(new Item(
                id,
                name,
                description,
                status,
                user,
                null
        ));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        repository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public Item updateItem(Long userId, Long id, String name, String description, Boolean available) {
        Item updateItem = repository.getById(id);
        if (updateItem == null) {
            throw new DataNotFoundException("Вещь с таким id не найдена.");
        }
        if (!Objects.equals(updateItem.getOwner().getId(), userId)) {
            throw new DataNotFoundException("Не трогайте чужое!");
        }
        if (name != null) {
            updateItem.setName(name);
        }
        if (description != null) {
            updateItem.setDescription(description);
        }
        Status status;
        if (available != null) {
            if (available) {
                status = Status.AVAILABLE;
            } else {
                status = Status.UNAVAILABLE;
            }
            updateItem.setAvailable(status);
        }
        return repository.save(updateItem);
    }

    @Override
    public Item getItemById(Long userId, long itemId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        Item result = repository.findById(itemId).orElse(null);
        if (result == null) {
            throw new DataNotFoundException("Вещь с таким id не найдена.");
        }
        return result;
    }

    @Override
    public List<Item> findItemsByOwnerId(Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Владелец вещи не найден");
        }
        return repository.findByOwnerId(userId);
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.searchItem(text);
    }

    @Override
    public List<Comment> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }

    @Override
    public boolean userIsOwnerOfItem(long userId, Long itemId) {
        if (userService.getUserById(userId).getId() == repository.getReferenceById(itemId).getOwner().getId()) {
            return true;
        }
        return false;
    }
}
