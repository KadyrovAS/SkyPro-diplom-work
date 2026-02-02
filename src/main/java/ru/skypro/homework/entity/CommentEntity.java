package ru.skypro.homework.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность (Entity) для представления комментария в базе данных.
 * Соответствует таблице "comments" в базе данных и содержит информацию о комментарии,
 * включая связи с объявлением и автором комментария.
 *
 * @author Сущность комментария
 * @version 1.0
 *
 * @see AdEntity
 * @see UserEntity
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {
    /** Уникальный идентификатор комментария, генерируется автоматически */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Текст комментария, обязательное поле */
    @Column(nullable = false)
    private String text;

    /** Дата и время создания комментария */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Объявление, к которому относится комментарий.
     * Связь многие-к-одному с сущностью объявления (AdEntity).
     * Загрузка по требованию (lazy loading) для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private AdEntity ad;

    /**
     * Автор комментария.
     * Связь многие-к-одному с сущностью пользователя (UserEntity).
     * Загрузка по требованию (lazy loading) для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;
}