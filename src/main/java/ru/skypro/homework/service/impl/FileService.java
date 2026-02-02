package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final Path rootLocation = Paths.get("uploads");

    public String saveImage(MultipartFile file, String subdir) throws IOException {
        // Создаем директорию, если она не существует
        Path directory = rootLocation.resolve(subdir);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // Генерируем уникальное имя файла
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + extension;
        Path destination = directory.resolve(filename);

        // Сохраняем файл
        Files.copy(file.getInputStream(), destination);

        log.info("Файл сохранен: {}", destination);
        // Изменено: возвращаем только имя файла без пути
        return filename;
    }

    public byte[] loadImage(String subdir, String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IOException("Имя файла не указано");
        }

        Path filePath = rootLocation.resolve(subdir).resolve(filename);
        if (!Files.exists(filePath)) {
            throw new IOException("Файл не найден: " + filePath);
        }

        return Files.readAllBytes(filePath);
    }

    public void deleteImage(String subdir, String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        Path filePath = rootLocation.resolve(subdir).resolve(filename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Файл удален: {}", filePath);
        }
    }

    // Метод для инициализации корневой директории при запуске
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                log.info("Создана корневая директория для загрузок: {}", rootLocation);
            }
        } catch (IOException e) {
            log.error("Не удалось создать корневую директорию: {}", e.getMessage());
        }
    }
}