package ru.practicum.shareit.gateway.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @Email(message = "Email должен быть корректным адресом электронной почты")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;
}
