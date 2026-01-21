package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

/**
 * Сервис аутентификации и регистрации пользователей.
 * Обеспечивает функционал входа в систему и регистрации новых пользователей.
 *
 * @author Система аутентификации
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Выполняет аутентификацию пользователя.
     * Проверяет соответствие введенных учетных данных (логин и пароль) данным в системе.
     *
     * @param userName логин пользователя
     * @param password пароль пользователя
     * @return true - если аутентификация успешна, false - в противном случае
     */
    @Override
    public boolean login(String userName, String password) {
        return userRepository.findByEmail(userName)
                .map(user -> {
                    boolean matches = passwordEncoder.matches(password, user.getPassword());
                    log.info("Попытка входа пользователя {}: {}", userName, matches ? "успешно" : "неудачно");
                    return matches;
                })
                .orElse(false);
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Проверяет уникальность email, кодирует пароль и сохраняет пользователя в БД.
     * Устанавливает роль USER по умолчанию, если роль не указана.
     *
     * @param register DTO с данными для регистрации пользователя
     * @return true - если регистрация успешна, false - если пользователь с таким email уже существует
     */
    @Override
    public boolean register(Register register) {
        if (userRepository.existsByEmail(register.getUsername())) {
            log.warn("Попытка регистрации существующего пользователя: {}", register.getUsername());
            return false;
        }

        try {
            UserEntity userEntity = userMapper.toEntity(register);

            // Кодируем пароль перед сохранением
            String encodedPassword = passwordEncoder.encode(register.getPassword());
            userEntity.setPassword(encodedPassword);

            // Устанавливаем роль по умолчанию, если не указана
            if (userEntity.getRole() == null) {
                userEntity.setRole(Role.USER);
            }

            userRepository.save(userEntity);
            log.info("Пользователь успешно зарегистрирован: {}", register.getUsername());
            return true;

        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя {}: {}", register.getUsername(), e.getMessage());
            return false;
        }
    }
}