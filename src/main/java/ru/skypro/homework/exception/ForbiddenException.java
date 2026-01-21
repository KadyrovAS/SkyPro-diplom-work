package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при попытке доступа к ресурсу без необходимых прав.
 * Соответствует HTTP статусу 403 (Forbidden).
 * Используется для обработки ситуаций, когда аутентифицированный пользователь
 * пытается выполнить операцию, на которую у него нет прав доступа.
 *
 * @author Исключение для запрещенных операций
 * @version 1.0
 *
 * @see RuntimeException
 * @see HttpStatus#FORBIDDEN
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message детальное сообщение об ошибке, объясняющее причину запрета доступа
     */
    public ForbiddenException(String message) {
        super(message);
    }
}