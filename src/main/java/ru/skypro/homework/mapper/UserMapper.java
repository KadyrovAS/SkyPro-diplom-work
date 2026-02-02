package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

/**
 * Маппер для преобразования между сущностью пользователя (UserEntity) и DTO пользователей.
 * Обеспечивает преобразование данных между слоем базы данных и слоем представления для пользователей.
 *
 * @author Система маппинга пользователей
 * @version 1.0
 */
@Component
public class UserMapper {

    /**
     * Преобразует DTO регистрации в сущность пользователя.
     * Создает новый объект UserEntity на основе данных из Register DTO.
     * Используется при регистрации нового пользователя.
     *
     * @param register DTO с данными для регистрации пользователя
     * @return новая сущность пользователя
     */
    public UserEntity toEntity(Register register) {
        UserEntity entity = new UserEntity();
        entity.setEmail(register.getUsername());
        entity.setPassword(register.getPassword());
        entity.setFirstName(register.getFirstName());
        entity.setLastName(register.getLastName());
        entity.setPhone(register.getPhone());
        entity.setRole(register.getRole());
        return entity;
    }

    /**
     * Преобразует сущность пользователя в DTO пользователя.
     * Создает объект User на основе данных из UserEntity.
     *
     * @param entity сущность пользователя из базы данных
     * @return DTO пользователя для передачи клиенту
     */
    public User toDto(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setEmail(entity.getEmail());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setPhone(entity.getPhone());
        user.setRole(entity.getRole());

        // Изменено: возвращаем URL для получения изображения через контроллер
        if (entity.getImage() != null && !entity.getImage().isEmpty()) {
            user.setImage("/users/" + entity.getId() + "/image");
        } else {
            user.setImage("");
        }
        return user;
    }

    /**
     * Обновляет существующую сущность пользователя данными из DTO обновления.
     * Используется для обновления профиля пользователя (имя, фамилия, телефон).
     * Обновляет только те поля, которые не являются null в DTO.
     *
     * @param dto DTO с новыми данными пользователя
     * @param entity сущность пользователя для обновления
     */
    public void updateEntity(UpdateUser dto, UserEntity entity) {
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
    }
}