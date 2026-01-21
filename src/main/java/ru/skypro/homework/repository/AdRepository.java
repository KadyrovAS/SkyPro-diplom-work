package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;

import java.util.List;

/**
 * Репозиторий для работы с объявлениями (сущность AdEntity).
 * Предоставляет методы для выполнения операций CRUD и поиска объявлений в базе данных.
 * Наследует функциональность JpaRepository для стандартных операций с базой данных.
 *
 * @author Репозиторий объявлений
 * @version 1.0
 *
 * @see AdEntity
 * @see UserEntity
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface AdRepository extends JpaRepository<AdEntity, Integer> {

    /**
     * Находит все объявления, созданные указанным пользователем.
     *
     * @param author сущность пользователя-автора объявлений
     * @return список объявлений, принадлежащих указанному автору
     *
     * @see UserEntity
     * @see AdEntity
     */
    List<AdEntity> findByAuthor(UserEntity author);

    /**
     * Находит все объявления, заголовок которых содержит указанную подстроку (без учета регистра).
     * Поиск выполняется по подстроке, содержащейся в заголовке объявления.
     *
     * @param title подстрока для поиска в заголовках объявлений
     * @return список объявлений, заголовки которых содержат указанную подстроку
     *
     * @see AdEntity
     */
    List<AdEntity> findByTitleContainingIgnoreCase(String title);
}