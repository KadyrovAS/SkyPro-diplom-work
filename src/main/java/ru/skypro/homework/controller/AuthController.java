package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.service.AuthService;

import javax.validation.Valid;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * Обрабатывает запросы на вход в систему и регистрацию новых пользователей.
 *
 * @author Контроллер аутентификации
 * @version 1.0
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Выполняет аутентификацию пользователя.
     * Проверяет учетные данные пользователя и устанавливает сессию.
     *
     * @param login DTO с данными для входа (логин и пароль)
     * @return ResponseEntity со статусом 200 при успешной аутентификации,
     *         или 401 при неверных учетных данных
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        log.info("Попытка входа пользователя: {}", login.getUsername());
        if (authService.login(login.getUsername(), login.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Создает учетную запись пользователя с предоставленными данными.
     *
     * @param register DTO с данными для регистрации
     * @return ResponseEntity со статусом 201 при успешной регистрации,
     *         или 400 при ошибке регистрации (например, пользователь уже существует)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Register register) {
        log.info("Попытка регистрации пользователя: {}", register.getUsername());
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}