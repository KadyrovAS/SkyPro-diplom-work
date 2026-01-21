package ru.skypro.homework.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.UserRepository;

/**
 * Компонент для инициализации тестовых данных в базе данных.
 * Создает стандартных пользователей (USER и ADMIN) при запуске приложения, если они не существуют.
 * Используется только для тестирования и разработки.
 *
 * @author Система инициализации данных
 * @version 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод, выполняемый при запуске приложения.
     * Создает тестового пользователя и администратора, если они отсутствуют в базе данных.
     *
     * @param args аргументы командной строки
     * @throws Exception если произошла ошибка при инициализации данных
     */
    @Override
    public void run(String... args) throws Exception {
        // Создаем тестового пользователя, если его нет
        if (userRepository.findByEmail("user@gmail.com").isEmpty()) {
            UserEntity user = new UserEntity();
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setFirstName("Иван");
            user.setLastName("Иванов");
            user.setPhone("+79991234567");
            user.setRole(Role.USER);
            userRepository.save(user);
            log.info("Создан тестовый пользователь: user@gmail.com");
        }

        // Создаем тестового администратора, если его нет
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Администратор");
            admin.setLastName("Системный");
            admin.setPhone("+79998887766");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            log.info("Создан тестовый администратор: admin@gmail.com");
        }
    }
}