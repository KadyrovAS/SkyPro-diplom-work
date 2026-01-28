package ru.skypro.homework.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


/**
 * DTO (Data Transfer Object) для представления расширенной информации об объявлении.
 * Содержит полную информацию об объявлении, включая контактные данные автора.
 * Используется для отображения детальной страницы объявления.
 *
 * @author DTO расширенного объявления
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ExtendedAd {
    /** Идентификатор объявления */
    private Integer pk;

    /** Имя автора объявления */
    private String authorFirstName;

    /** Фамилия автора объявления */
    private String authorLastName;

    /** Подробное описание объявления */
    private String description;

    /** Email автора объявления */
    private String email;

    /** Ссылка на изображение объявления */
    private String image;

    /** Телефон автора объявления */
    private String phone;

    /** Цена объявления */
    private Integer price;

    /** Заголовок объявления */
    private String title;
}