package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdService;

import javax.validation.Valid;

/**
 * Контроллер для управления объявлениями.
 * Обрабатывает HTTP запросы, связанные с созданием, получением, обновлением и удалением объявлений.
 * Поддерживает загрузку и получение изображений для объявлений.
 *
 * @author Контроллер объявлений
 * @version 1.0
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @GetMapping("/ads")
    public ResponseEntity<Ads> getAllAds() {
        Ads ads = adService.getAllAds();
        return ResponseEntity.ok(ads);
    }

    @PostMapping(value = "/ads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(@RequestPart("properties") @Valid CreateOrUpdateAd properties,
                                    @RequestPart("image") MultipartFile image,
                                    Authentication authentication) {
        Ad ad = adService.addAd(properties, image, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
    }

    @GetMapping("/ads/{id}")
    public ResponseEntity<ExtendedAd> getAd(@PathVariable Integer id) {
        ExtendedAd extendedAd = adService.getAd(id);
        return ResponseEntity.ok(extendedAd);
    }

    /**
     * Удаляет объявление по его идентификатору.
     * Только автор объявления или администратор могут удалить объявление.
     * Использует аннотацию @PreAuthorize для проверки прав на уровне контроллера.
     *
     * @param id идентификатор объявления
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity со статусом 204 (No Content)
     */
    @DeleteMapping("/ads/{id}")
    @PreAuthorize("hasRole('ADMIN') or @adServiceImpl.isAdAuthor(#id, authentication)")
    public ResponseEntity<?> deleteAd(@PathVariable Integer id,
                                      Authentication authentication) {
        adService.deleteAd(id, authentication);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет информацию об объявлении.
     * Только автор объявления или администратор могут обновить объявление.
     * Использует аннотацию @PreAuthorize для проверки прав на уровне контроллера.
     *
     * @param id идентификатор объявления
     * @param updateAd новые данные для обновления объявления
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity с обновленным объявлением
     */
    @PatchMapping("/ads/{id}")
    @PreAuthorize("hasRole('ADMIN') or @adServiceImpl.isAdAuthor(#id, authentication)")
    public ResponseEntity<Ad> updateAd(@PathVariable Integer id,
                                       @Valid @RequestBody CreateOrUpdateAd updateAd,
                                       Authentication authentication) {
        Ad ad = adService.updateAd(id, updateAd, authentication);
        return ResponseEntity.ok(ad);
    }

    @GetMapping("/ads/me")
    public ResponseEntity<Ads> getMyAds(Authentication authentication) {
        Ads ads = adService.getMyAds(authentication);
        return ResponseEntity.ok(ads);
    }

    /**
     * Обновляет изображение объявления.
     * Только автор объявления или администратор могут обновить изображение.
     * Использует аннотацию @PreAuthorize для проверки прав на уровне контроллера.
     *
     * @param id идентификатор объявления
     * @param image новый файл изображения
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity со статусом 200 (OK)
     */
    @PatchMapping(value = "/ads/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or @adServiceImpl.isAdAuthor(#id, authentication)")
    public ResponseEntity<?> updateAdImage(@PathVariable Integer id,
                                           @RequestParam("image") MultipartFile image,
                                           Authentication authentication) {
        adService.updateAdImage(id, image, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/ads/{id}/image", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getAdImage(@PathVariable Integer id) {
        byte[] image = adService.getAdImage(id);
        if (image.length > 0) {
            return ResponseEntity.ok(image);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}