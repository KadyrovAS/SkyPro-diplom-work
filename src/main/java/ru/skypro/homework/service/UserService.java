package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

import java.io.IOException;

public interface UserService {

    /**
     * Получение информации об авторизованном пользователе
     *
     * @param authentication объект аутентификации
     * @return DTO пользователя
     */
    User getCurrentUser(Authentication authentication);

    /**
     * Обновление информации об авторизованном пользователе
     *
     * @param updateUser DTO с обновленными данными
     * @param authentication объект аутентификации
     * @return обновленные данные пользователя
     */
    UpdateUser updateUser(UpdateUser updateUser, Authentication authentication);

    /**
     * Обновление пароля пользователя
     *
     * @param newPassword DTO с текущим и новым паролем
     * @param authentication объект аутентификации
     */
    void updatePassword(NewPassword newPassword, Authentication authentication);

    /**
     * Обновление аватара пользователя
     *
     * @param image файл изображения
     * @param authentication объект аутентификации
     * @throws IOException при ошибке работы с файлом
     */
    void updateUserImage(MultipartFile image, Authentication authentication) throws IOException;

    /**
     * Получение изображения пользователя по ID
     *
     * @param userId ID пользователя
     * @return массив байтов изображения
     */
    byte[] getUserImage(Integer userId);
    public boolean userExists(String email);
    public UserEntity getUserByEmail(String email);
}