package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.UserService;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Контроллер для управления профилем пользователя.
 * Обрабатывает запросы на получение и обновление информации о пользователе,
 * изменение пароля и управление аватаром пользователя.
 *
 * @author Контроллер пользователей
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Обновляет пароль текущего пользователя.
     * Требует аутентификации и проверки текущего пароля.
     *
     * @param newPassword DTO с текущим и новым паролем
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity со статусом 200 при успешном изменении,
     *         или 403 при неверном текущем пароле
     */
    @Operation(
            summary = "Обновление пароля",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PostMapping("/users/set_password")
    public ResponseEntity<?> setPassword(@Valid @RequestBody NewPassword newPassword,
                                         Authentication authentication) {
        try {
            userService.updatePassword(newPassword, authentication);
            log.info("Пароль успешно изменен для пользователя: {}", authentication.getName());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Ошибка при изменении пароля: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity с информацией о пользователе или статусом 401, если пользователь не аутентифицирован
     */
    @Operation(
            summary = "Получение информации об авторизованном пользователе",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/users/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        if (user != null) {
            log.info("Получена информация о пользователе: {}", authentication.getName());
            return ResponseEntity.ok(user);
        } else {
            log.warn("Пользователь не найден: {}", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Обновляет информацию о текущем пользователе.
     * Позволяет изменить имя, фамилию и телефон пользователя.
     *
     * @param updateUser DTO с новыми данными пользователя
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity с обновленными данными пользователя
     */
    @Operation(
            summary = "Обновление информации об авторизованном пользователе",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateUser.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PatchMapping("/users/me")
    public ResponseEntity<UpdateUser> updateUser(@Valid @RequestBody UpdateUser updateUser,
                                                 Authentication authentication) {
        UpdateUser updatedUser = userService.updateUser(updateUser, authentication);
        if (updatedUser != null) {
            log.info("Данные пользователя обновлены: {}", authentication.getName());
            return ResponseEntity.ok(updatedUser);
        } else {
            log.warn("Не удалось обновить данные пользователя: {}", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Обновляет аватар текущего пользователя.
     * Принимает изображение в формате multipart/form-data.
     *
     * @param image файл изображения для аватара
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity со статусом 200 при успешном обновлении,
     *         или соответствующим кодом ошибки при возникновении проблем
     */
    @Operation(
            summary = "Обновление аватара авторизованного пользователя",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PatchMapping(value = "/users/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserImage(@RequestParam("image") MultipartFile image,
                                             Authentication authentication) {
        try {
            userService.updateUserImage(image, authentication);
            log.info("Аватар пользователя обновлен: {}", authentication.getName());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Ошибка при обновлении аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при сохранении изображения");
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении аватара: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Получает аватар пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return ResponseEntity с изображением аватара или статусом 404, если аватар не найден
     */
    @Operation(
            summary = "Получение аватара пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping(value = "/users/{id}/image", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getUserImage(@PathVariable Integer id) {
        byte[] image = userService.getUserImage(id);
        if (image != null && image.length > 0) {
            return ResponseEntity.ok(image);
        } else {
            log.warn("Аватар пользователя с ID {} не найден", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}