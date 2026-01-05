package ru.skypro.homework.dto;

import javax.validation.constraints.*;
import lombok.Data;

@Data
public class Login {
    @Size(min = 4, max = 32, message = "Логин должен быть от 4 до 32 символов")
    private String username;

    @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
    private String password;
}