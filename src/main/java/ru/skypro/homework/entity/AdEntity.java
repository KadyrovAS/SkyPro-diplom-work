package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность (Entity) для представления объявления в базе данных.
 * Соответствует таблице "ads" в базе данных и содержит информацию об объявлении,
 * включая связи с автором (пользователем) и комментариями.
 *
 * @author Сущность объявления
 * @version 1.0
 *
 * @see UserEntity
 * @see CommentEntity
 */
@Entity
@Table(name = "ads")
@Data
public class AdEntity {
    /** Уникальный идентификатор объявления, генерируется автоматически */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Заголовок объявления, обязательное поле */
    @Column(nullable = false)
    private String title;

    /** Цена объявления */
    private Integer price;

    /** Описание объявления, максимальная длина 1000 символов */
    @Column(length = 1000)
    private String description;

    /** Путь к изображению объявления в файловой системе */
    @Column(name = "image")
    private String image;

    /**
     * Автор объявления.
     * Связь многие-к-одному с сущностью пользователя (UserEntity).
     * Загрузка по требованию (lazy loading) для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;

    /**
     * Список комментариев к объявлению.
     * Связь один-ко-многим с сущностью комментария (CommentEntity).
     * Каскадное удаление и orphanRemoval обеспечивают удаление комментариев при удалении объявления.
     */
    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;
}