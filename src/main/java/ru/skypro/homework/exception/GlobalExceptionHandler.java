package ru.skypro.homework.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Перехватывает исключения, выбрасываемые в контроллерах, и преобразует их
 * в структурированные HTTP-ответы с соответствующими статус-кодами.
 * Обеспечивает единообразную обработку ошибок и логирование исключений.
 *
 * @author Глобальный обработчик исключений
 * @version 1.0
 *
 * @see RestControllerAdvice
 * @see ExceptionHandler
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения NotFoundException.
     * Возвращает HTTP статус 404 (Not Found) с информацией об отсутствующем ресурсе.
     *
     * @param e исключение NotFoundException
     * @return ResponseEntity с HTTP статусом 404 и деталями ошибки
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        log.warn("Ресурс не найден: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("status", "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Обрабатывает исключения ForbiddenException.
     * Возвращает HTTP статус 403 (Forbidden) с информацией о запрете доступа.
     *
     * @param e исключение ForbiddenException
     * @return ResponseEntity с HTTP статусом 403 и деталями ошибки
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(ForbiddenException e) {
        log.warn("Доступ запрещен: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("status", "403");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Обрабатывает исключения BadRequestException.
     * Возвращает HTTP статус 400 (Bad Request) с информацией о некорректном запросе.
     *
     * @param e исключение BadRequestException
     * @return ResponseEntity с HTTP статусом 400 и деталями ошибки
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(BadRequestException e) {
        log.warn("Некорректный запрос: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("status", "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает исключения MethodArgumentNotValidException.
     * Возвращает HTTP статус 400 (Bad Request) с информацией об ошибках валидации.
     * Собирает все ошибки валидации полей и возвращает их в структурированном виде.
     *
     * @param e исключение MethodArgumentNotValidException
     * @return ResponseEntity с HTTP статусом 400 и списком ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("Ошибка валидации: {}", errors);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ошибка валидации данных");
        response.put("errors", errors.toString());
        response.put("status", "400");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает исключения ConstraintViolationException.
     * Возвращает HTTP статус 400 (Bad Request) с информацией о нарушении ограничений.
     *
     * @param e исключение ConstraintViolationException
     * @return ResponseEntity с HTTP статусом 400 и деталями ошибки
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Нарушение ограничений: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("status", "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает исключения AuthenticationException.
     * Возвращает HTTP статус 401 (Unauthorized) при ошибках аутентификации.
     *
     * @param e исключение AuthenticationException
     * @return ResponseEntity с HTTP статусом 401 и информацией об ошибке аутентификации
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Ошибка аутентификации: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Неверные учетные данные");
        response.put("status", "401");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Обрабатывает исключения AccessDeniedException.
     * Возвращает HTTP статус 403 (Forbidden) при попытке доступа без необходимых прав.
     *
     * @param e исключение AccessDeniedException
     * @return ResponseEntity с HTTP статусом 403 и информацией о недостатке прав
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Доступ запрещен: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Недостаточно прав для выполнения операции");
        response.put("status", "403");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Обрабатывает исключения MaxUploadSizeExceededException.
     * Возвращает HTTP статус 400 (Bad Request) при превышении максимального размера файла.
     *
     * @param e исключение MaxUploadSizeExceededException
     * @return ResponseEntity с HTTP статусом 400 и информацией о превышении размера файла
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException e) {
        log.warn("Превышен максимальный размер файла: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Превышен максимальный размер файла (10MB)");
        response.put("status", "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает исключения IllegalArgumentException.
     * Возвращает HTTP статус 400 (Bad Request) при передаче некорректных аргументов.
     *
     * @param e исключение IllegalArgumentException
     * @return ResponseEntity с HTTP статусом 400 и информацией о некорректных аргументах
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Некорректный аргумент: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("status", "400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает исключения RuntimeException.
     * Возвращает HTTP статус 500 (Internal Server Error) для непредвиденных ошибок выполнения.
     * Используется как обработчик по умолчанию для всех Runtime исключений, не перехваченных другими обработчиками.
     *
     * @param e исключение RuntimeException
     * @return ResponseEntity с HTTP статусом 500 и информацией о внутренней ошибке сервера
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("Ошибка выполнения: {}", e.getMessage(), e);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Внутренняя ошибка сервера: " + e.getMessage());
        response.put("status", "500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Обрабатывает все остальные исключения (Exception).
     * Возвращает HTTP статус 500 (Internal Server Error) для непредвиденных ошибок.
     * Является последним уровнем обработки исключений, перехватывает все исключения, не обработанные ранее.
     *
     * @param e исключение Exception
     * @return ResponseEntity с HTTP статусом 500 и общей информацией о внутренней ошибке сервера
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Непредвиденная ошибка: {}", e.getMessage(), e);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Внутренняя ошибка сервера");
        response.put("status", "500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}