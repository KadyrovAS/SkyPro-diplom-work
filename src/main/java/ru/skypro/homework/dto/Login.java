package ru.skypro.homework.dto;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


/**
 * DTO (Data Transfer Object) для аутентификации пользователя.
 * Используется при входе пользователя в систему.
 * Содержит валидационные аннотации для проверки корректности учетных данных.
 *
 * @author DTO аутентификации
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Login {
    /** Логин пользователя (email) (от 4 до 32 символов) */
    @Size(min = 4, max = 32, message = "Логин должен быть от 4 до 32 символов")
    private String username;

    /** Пароль пользователя (от 8 до 16 символов) */
    @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
    private String password;
}