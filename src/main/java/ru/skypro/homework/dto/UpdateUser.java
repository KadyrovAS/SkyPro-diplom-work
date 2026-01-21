package ru.skypro.homework.dto;

import javax.validation.constraints.*;
import lombok.Data;

/**
 * DTO (Data Transfer Object) для обновления профиля пользователя.
 * Используется при изменении информации о текущем пользователе.
 * Содержит валидационные аннотации для проверки корректности обновляемых данных.
 *
 * @author DTO обновления пользователя
 * @version 1.0
 */
@Data
public class UpdateUser {
    /** Имя пользователя (от 3 до 10 символов) */
    @Size(min = 3, max = 10, message = "Имя должно быть от 3 до 10 символов")
    private String firstName;

    /** Фамилия пользователя (от 3 до 10 символов) */
    @Size(min = 3, max = 10, message = "Фамилия должна быть от 3 до 10 символов")
    private String lastName;

    /** Телефон пользователя в формате +7 XXX XXX-XX-XX */
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}",
            message = "Номер телефона должен соответствовать формату: +7 XXX XXX-XX-XX")
    private String phone;
}