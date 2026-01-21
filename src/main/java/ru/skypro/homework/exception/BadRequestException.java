package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при некорректном запросе от клиента.
 * Соответствует HTTP статусу 400 (Bad Request).
 * Используется для обработки ситуаций, когда клиент отправляет запрос с недопустимыми параметрами
 * или данными, которые не могут быть обработаны сервером.
 *
 * @author Исключение для некорректных запросов
 * @version 1.0
 *
 * @see RuntimeException
 * @see HttpStatus#BAD_REQUEST
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message детальное сообщение об ошибке, которое будет возвращено клиенту
     */
    public BadRequestException(String message) {
        super(message);
    }
}