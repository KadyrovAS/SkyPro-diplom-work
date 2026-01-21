package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.UserEntity;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями (сущность UserEntity).
 * Предоставляет методы для выполнения операций CRUD и поиска пользователей в базе данных.
 * Наследует функциональность JpaRepository для стандартных операций с базой данных.
 *
 * @author Репозиторий пользователей
 * @version 1.0
 *
 * @see UserEntity
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    /**
     * Находит пользователя по его email адресу.
     *
     * @param email email адрес пользователя для поиска
     * @return Optional, содержащий пользователя, если найден, или пустой Optional
     *
     * @see UserEntity
     * @see Optional
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным email адресом.
     *
     * @param email email адрес для проверки
     * @return true - если пользователь с указанным email существует, false - в противном случае
     *
     * @see UserEntity
     */
    boolean existsByEmail(String email);
}