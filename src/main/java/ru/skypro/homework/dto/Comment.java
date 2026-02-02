package ru.skypro.homework.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO (Data Transfer Object) для представления комментария к объявлению.
 * Содержит информацию о комментарии и его авторе.
 *
 * @author DTO комментария
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    /** Идентификатор автора комментария */
    private Integer author;

    /** Ссылка на аватар автора комментария */
    private String authorImage;

    /** Имя автора комментария */
    private String authorFirstName;

    /** Дата и время создания комментария в миллисекундах с 01.01.1970 */
    private Long createdAt;

    /** Идентификатор комментария (primary key) */
    private Integer pk;

    /** Текст комментария */
    private String text;
}