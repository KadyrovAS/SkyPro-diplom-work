package ru.skypro.homework.dto;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO (Data Transfer Object) для изменения пароля пользователя.
 * Используется при обновлении пароля текущего пользователя.
 * Содержит валидационные аннотации для проверки корректности паролей.
 *
 * @author DTO изменения пароля
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class NewPassword {
    /** Текущий пароль пользователя (от 8 до 16 символов) */
    @Size(min = 8, max = 16, message = "Длина пароля должна быть от 8 до 16 символов")
    private String currentPassword;

    /** Новый пароль пользователя (от 8 до 16 символов) */
    @Size(min = 8, max = 16, message = "Длина пароля должна быть от 8 до 16 символов")
    private String newPassword;
}