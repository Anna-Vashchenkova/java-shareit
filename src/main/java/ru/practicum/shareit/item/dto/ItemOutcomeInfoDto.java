package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemOutcomeInfoDto {
    private Long id;
    @NotBlank(message = "Название вещи не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentDto {
        private long id;
        @NotBlank(message = "Комментарий не может быть пустым.")
        private String text;
        private String authorName;
        private LocalDateTime created;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingDto {
        private long id;
        private long bookerId;
        private LocalDateTime start;
        private LocalDateTime end;

    }
}
