package ru.skypro.homework.dto;

import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


/**
 * DTO (Data Transfer Object) для создания или обновления комментария.
 * Используется при добавлении нового комментария или изменении существующего.
 * Содержит валидационные аннотации для проверки корректности текста комментария.
 *
 * @author DTO создания/обновления комментария
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CreateOrUpdateComment {
    /** Текст комментария (обязательное поле, от 8 до 64 символов) */
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 8, max = 64, message = "Текст комментария должен быть от 8 до 64 символов")
    private String text;
}