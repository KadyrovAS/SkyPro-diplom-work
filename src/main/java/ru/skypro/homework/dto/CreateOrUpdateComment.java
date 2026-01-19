package ru.skypro.homework.dto;

import javax.validation.constraints.*;
import lombok.Data;

@Data
public class CreateOrUpdateComment {
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 8, max = 64, message = "Текст комментария должен быть от 8 до 64 символов")
    private String text;
}