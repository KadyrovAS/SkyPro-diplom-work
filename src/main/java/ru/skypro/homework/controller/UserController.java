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

import javax.validation.Valid;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class UserController {
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
        // TODO: реализовать в сервисе
        log.info("Изменение пароля для пользователя: {}", authentication.getName());
        return ResponseEntity.ok().build();
    }

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
        // TODO: реализовать в сервисе
        log.info("Получение информации о пользователе: {}", authentication.getName());

        // Заглушка
        User user = new User();
        user.setId(1);
        user.setEmail(authentication.getName());
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        user.setPhone("+79991234567");
        user.setRole(Role.USER);
        user.setImage("/users/1/image");

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/users/me")
    public ResponseEntity<UpdateUser> updateUser(@Valid @RequestBody UpdateUser updateUser,
                                                 Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Обновление пользователя {}: {}", authentication.getName(), updateUser);
        return ResponseEntity.ok(updateUser);
    }

    @PatchMapping(value = "/users/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserImage(@RequestParam("image") MultipartFile image,
                                             Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Обновление аватара пользователя: {}", authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/users/{id}/image", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getUserImage(@PathVariable Integer id) {
        // TODO: реализовать в сервисе
        log.info("Получение изображения пользователя с id: {}", id);
        // Возвращаем заглушку - пустой массив байтов
        return ResponseEntity.ok(new byte[0]);
    }
}