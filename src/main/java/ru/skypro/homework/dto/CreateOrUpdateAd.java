package ru.skypro.homework.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.*;

/**
 * DTO (Data Transfer Object) для создания или обновления объявления.
 * Используется при добавлении нового объявления или изменении существующего.
 * Содержит валидационные аннотации для проверки корректности данных.
 *
 * @author DTO создания/обновления объявления
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateAd {
    /** Заголовок объявления (от 4 до 32 символов) */
    @Size(min = 4, max = 32)
    private String title;

    /** Цена объявления (от 0 до 10 000 000 рублей) */
    @Min(0)
    @Max(10000000)
    private Integer price;

    /** Описание объявления (от 8 до 64 символов) */
    @Size(min = 8, max = 64)
    private String description;
}