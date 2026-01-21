package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.BadRequestException;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

/**
 * Сервис для работы с пользователями.
 * Обеспечивает управление профилем пользователя, включая обновление данных,
 * изменение пароля, управление аватаром и получение информации о пользователе.
 *
 * @author Система управления пользователями
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link User} DTO с данными пользователя
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + email));

        log.info("Получена информация о пользователе: {}", email);
        return userMapper.toDto(userEntity);
    }

    /**
     * Обновляет информацию о текущем пользователе.
     * Валидирует входные данные перед сохранением.
     *
     * @param updateUser      DTO с новыми данными пользователя
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link UpdateUser} DTO с обновленными данными
     * @throws NotFoundException   если пользователь не найден
     * @throws BadRequestException если данные не проходят валидацию
     */
    @Override
    public UpdateUser updateUser(UpdateUser updateUser, Authentication authentication) {
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + email));

        // Валидация входных данных
        if (updateUser.getFirstName() != null &&
                (updateUser.getFirstName().length() < 3 || updateUser.getFirstName().length() > 10)) {
            throw new BadRequestException("Имя должно быть от 3 до 10 символов");
        }

        if (updateUser.getLastName() != null &&
                (updateUser.getLastName().length() < 3 || updateUser.getLastName().length() > 10)) {
            throw new BadRequestException("Фамилия должна быть от 3 до 10 символов");
        }

        if (updateUser.getPhone() != null &&
                !updateUser.getPhone().matches("\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")) {
            throw new BadRequestException("Номер телефона должен соответствовать формату: +7 XXX XXX-XX-XX");
        }

        userMapper.updateEntity(updateUser, userEntity);
        UserEntity savedEntity = userRepository.save(userEntity);

        // Возвращаем обновленные данные
        UpdateUser result = new UpdateUser();
        result.setFirstName(savedEntity.getFirstName());
        result.setLastName(savedEntity.getLastName());
        result.setPhone(savedEntity.getPhone());

        log.info("Данные пользователя обновлены: {}", email);
        return result;
    }

    /**
     * Обновляет пароль текущего пользователя.
     * Проверяет корректность текущего пароля и валидирует новый пароль.
     *
     * @param newPassword    DTO с текущим и новым паролями
     * @param authentication объект аутентификации текущего пользователя
     * @throws NotFoundException   если пользователь не найден
     * @throws BadRequestException если пароли не проходят валидацию
     * @throws ForbiddenException  если текущий пароль неверен
     */
    @Override
    public void updatePassword(NewPassword newPassword, Authentication authentication) {
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + email));

        // Валидация паролей
        if (newPassword.getCurrentPassword() == null || newPassword.getCurrentPassword().length() < 8) {
            throw new BadRequestException("Текущий пароль должен быть не менее 8 символов");
        }

        if (newPassword.getNewPassword() == null || newPassword.getNewPassword().length() < 8) {
            throw new BadRequestException("Новый пароль должен быть не менее 8 символов");
        }

        // Проверяем текущий пароль
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword())) {
            throw new ForbiddenException("Текущий пароль неверен");
        }

        // Проверяем, что новый пароль отличается от старого
        if (passwordEncoder.matches(newPassword.getNewPassword(), userEntity.getPassword())) {
            throw new BadRequestException("Новый пароль должен отличаться от старого");
        }

        // Обновляем пароль
        String encodedNewPassword = passwordEncoder.encode(newPassword.getNewPassword());
        userEntity.setPassword(encodedNewPassword);
        userRepository.save(userEntity);

        log.info("Пароль пользователя изменен: {}", email);
    }

    /**
     * Обновляет аватар текущего пользователя.
     * Проверяет тип и размер файла перед сохранением.
     * Удаляет старый аватар, если он существует.
     *
     * @param image          файл с новым аватаром
     * @param authentication объект аутентификации текущего пользователя
     * @throws IOException         если произошла ошибка при работе с файлом
     * @throws NotFoundException   если пользователь не найден
     * @throws BadRequestException если файл не прошел валидацию
     */
    @Override
    public void updateUserImage(MultipartFile image, Authentication authentication) throws IOException {
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + email));

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Файл изображения отсутствует или пуст");
        }

        // Проверяем тип файла
        String contentType = image.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") && !contentType.equals("image/png") &&
                        !contentType.equals("image/jpg"))) {
            throw new BadRequestException("Разрешены только изображения в формате JPEG, JPG или PNG");
        }

        // Проверяем размер файла (10MB)
        if (image.getSize() > 10 * 1024 * 1024) {
            throw new BadRequestException("Размер файла не должен превышать 10MB");
        }

        // Сохраняем изображение
        String imagePath = fileService.saveImage(image, "users");

        // Удаляем старое изображение, если оно существует
        if (userEntity.getImage() != null && !userEntity.getImage().isEmpty()) {
            try {
                fileService.deleteImage(userEntity.getImage());
            } catch (IOException e) {
                log.error("Ошибка при удалении старого изображения пользователя {}: {}", email, e.getMessage());
            }
        }

        // Обновляем путь к изображению
        userEntity.setImage(imagePath);
        userRepository.save(userEntity);

        log.info("Аватар пользователя обновлен: {}", email);
    }

    /**
     * Получает аватар пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return массив байтов изображения аватара
     * @throws NotFoundException   если пользователь или аватар не найдены
     * @throws BadRequestException если произошла ошибка при загрузке изображения
     */
    @Override
    public byte[] getUserImage(Integer userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));

        if (userEntity.getImage() == null || userEntity.getImage().isEmpty()) {
            log.warn("Аватар пользователя с ID {} не найден", userId);
            throw new NotFoundException("Аватар пользователя не найден");
        }

        try {
            return fileService.loadImage(userEntity.getImage());
        } catch (IOException e) {
            log.error("Ошибка при чтении аватара пользователя {}: {}", userId, e.getMessage());
            throw new BadRequestException("Не удалось загрузить изображение: " + e.getMessage());
        }
    }

    /**
     * Проверяет существование пользователя по email.
     *
     * @param email email пользователя для проверки
     * @return true - если пользователь существует, false - в противном случае
     */
    @Override
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Получает сущность пользователя по email.
     *
     * @param email email пользователя
     * @return {@link UserEntity} сущность пользователя
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + email));
    }
}