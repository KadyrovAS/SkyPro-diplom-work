package ru.skypro.homework.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) для представления объявления в списке.
 * Содержит основную информацию об объявлении для отображения в списках.
 *
 * @author DTO объявления
 * @version 1.0
 */
@Data
public class Ad {
    /** Идентификатор автора объявления */
    private Integer author;

    /** Ссылка на изображение объявления (путь к файлу или URL) */
    private String image;

    /** Идентификатор объявления (primary key) */
    private Integer pk;

    /** Цена объявления в рублях */
    private Integer price;

    /** Заголовок объявления */
    private String title;
}