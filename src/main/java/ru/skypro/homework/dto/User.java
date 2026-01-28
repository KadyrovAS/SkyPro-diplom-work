package ru.skypro.homework.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO (Data Transfer Object) для представления информации о пользователе.
 * Содержит полную информацию о пользователе для отображения в профиле.
 *
 * @author DTO пользователя
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User {
    /** Идентификатор пользователя */
    private Integer id;

    /** Email пользователя (логин) */
    private String email;

    /** Имя пользователя */
    private String firstName;

    /** Фамилия пользователя */
    private String lastName;

    /** Телефон пользователя */
    private String phone;

    /** Роль пользователя в системе */
    private Role role;

    /** Ссылка на аватар пользователя */
    private String image;
}