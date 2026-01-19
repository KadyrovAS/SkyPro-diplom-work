package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class Comment {
    private Integer author;          // id автора комментария
    private String authorImage;      // ссылка на аватар автора комментария
    private String authorFirstName;  // имя создателя комментария
    private Long createdAt;          // дата и время в миллисекундах
    private Integer pk;              // id комментария
    private String text;             // текст комментария
}