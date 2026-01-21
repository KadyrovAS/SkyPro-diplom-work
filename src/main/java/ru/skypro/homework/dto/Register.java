package ru.skypro.homework.dto;

import javax.validation.constraints.*;
import lombok.Data;

/**
 * DTO (Data Transfer Object) для регистрации нового пользователя.
 * Используется при создании новой учетной записи пользователя.
 * Содержит валидационные аннотации для проверки корректности данных регистрации.
 *
 * @author DTO регистрации
 * @version 1.0
 */
@Data
public class Register {
    /** Логин пользователя (email) (от 4 до 32 символов) */
    @Size(min = 4, max = 32, message = "Логин должен быть от 4 до 32 символов")
    private String username;

    /** Пароль пользователя (от 8 до 16 символов) */
    @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
    private String password;

    /** Имя пользователя (от 2 до 16 символов) */
    @Size(min = 2, max = 16, message = "Имя должно быть от 2 до 16 символов")
    private String firstName;

    /** Фамилия пользователя (от 2 до 16 символов) */
    @Size(min = 2, max = 16, message = "Фамилия должна быть от 2 до 16 символов")
    private String lastName;

    /** Телефон пользователя в формате +7 XXX XXX-XX-XX */
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}",
            message = "Номер телефона должен соответствовать формату: +7 XXX XXX-XX-XX")
    private String phone;

    /** Роль пользователя (USER или ADMIN) */
    private Role role;
}