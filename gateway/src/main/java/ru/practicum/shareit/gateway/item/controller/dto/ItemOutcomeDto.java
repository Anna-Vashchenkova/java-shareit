package ru.practicum.shareit.gateway.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.user.controller.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemOutcomeDto {
    private Long id;
    @NotBlank(message = "Название вещи не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}
