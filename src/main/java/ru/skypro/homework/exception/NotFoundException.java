package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующему ресурсу.
 * Соответствует HTTP статусу 404 (Not Found).
 * Используется для обработки ситуаций, когда запрашиваемый ресурс
 * (пользователь, объявление, комментарий) не существует в системе.
 *
 * @author Исключение для отсутствующих ресурсов
 * @version 1.0
 *
 * @see RuntimeException
 * @see HttpStatus#NOT_FOUND
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message детальное сообщение об ошибке, указывающее на отсутствующий ресурс
     */
    public NotFoundException(String message) {
        super(message);
    }
}