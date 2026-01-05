package ru.skypro.homework.dto;

import javax.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateUser {
    @Size(min = 3, max = 10, message = "Имя должно быть от 3 до 10 символов")
    private String firstName;

    @Size(min = 3, max = 10, message = "Фамилия должна быть от 3 до 10 символов")
    private String lastName;

    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}",
            message = "Номер телефона должен соответствовать формату: +7 XXX XXX-XX-XX")
    private String phone;
}