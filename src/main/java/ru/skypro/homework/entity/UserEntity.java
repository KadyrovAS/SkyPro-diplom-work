package ru.skypro.homework.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность (Entity) для представления пользователя в базе данных.
 * Соответствует таблице "users" в базе данных и содержит информацию о пользователе,
 * включая учетные данные, персональные данные и связи с объявлениями и комментариями.
 *
 * @author Сущность пользователя
 * @version 1.0
 *
 * @see AdEntity
 * @see CommentEntity
 * @see Role
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    /** Уникальный идентификатор пользователя, генерируется автоматически */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Email пользователя, используется как логин, уникальное и обязательное поле */
    @Column(nullable = false, unique = true)
    private String email;

    /** Пароль пользователя в зашифрованном виде, обязательное поле */
    @Column(nullable = false)
    private String password;

    /** Имя пользователя */
    @Column(name = "first_name")
    private String firstName;

    /** Фамилия пользователя */
    @Column(name = "last_name")
    private String lastName;

    /** Телефон пользователя в формате +7 XXX XXX-XX-XX */
    private String phone;

    /** Роль пользователя в системе (USER или ADMIN), хранится как строка */
    @Enumerated(EnumType.STRING)
    private Role role;

    /** Путь к аватару пользователя в файловой системе */
    @Column(name = "image")
    private String image;

    /**
     * Список объявлений, созданных пользователем.
     * Связь один-ко-многим с сущностью объявления (AdEntity).
     * Каскадное сохранение обеспечивает сохранение объявлений при сохранении пользователя.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<AdEntity> ads;

    /**
     * Список комментариев, созданных пользователем.
     * Связь один-ко-многим с сущностью комментария (CommentEntity).
     * Каскадное сохранение обеспечивает сохранение комментариев при сохранении пользователя.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<CommentEntity> comments;
}