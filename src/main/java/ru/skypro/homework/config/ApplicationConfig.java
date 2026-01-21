package ru.skypro.homework.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import ru.skypro.homework.service.impl.FileService;

import javax.annotation.PostConstruct;

/**
 * Конфигурационный класс приложения.
 * Выполняет инициализацию необходимых компонентов при запуске приложения.
 *
 * @author Система управления объявлениями
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final FileService fileService;

    /**
     * Метод инициализации приложения.
     * Вызывается после создания всех бинов и выполняет начальную настройку файловой системы.
     * Создает необходимые директории для хранения изображений, если они не существуют.
     *
     * @throws RuntimeException если не удалось создать директории для загрузок
     */
    @PostConstruct
    public void init() {
        fileService.init();
    }
}