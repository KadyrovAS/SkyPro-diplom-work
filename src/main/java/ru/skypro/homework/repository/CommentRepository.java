package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;

/**
 * Репозиторий для работы с комментариями (сущность CommentEntity).
 * Предоставляет методы для выполнения операций CRUD и поиска комментариев в базе данных.
 * Наследует функциональность JpaRepository для стандартных операций с базой данных.
 *
 * @author Репозиторий комментариев
 * @version 1.0
 *
 * @see CommentEntity
 * @see AdEntity
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    /**
     * Находит все комментарии, относящиеся к указанному объявлению.
     *
     * @param ad сущность объявления, к которому относятся комментарии
     * @return список комментариев для указанного объявления
     *
     * @see AdEntity
     * @see CommentEntity
     */
    List<CommentEntity> findByAd(AdEntity ad);

    /**
     * Удаляет все комментарии, относящиеся к указанному объявлению.
     *
     * @param ad сущность объявления, комментарии к которому необходимо удалить
     *
     * @see AdEntity
     * @see CommentEntity
     */
    void deleteByAd(AdEntity ad);
}