package ru.skypro.homework.controller;

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
import java.nio.charset.StandardCharsets;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AdController {

    @GetMapping("/ads")
    public ResponseEntity<Ads> getAllAds() {
        // TODO: реализовать в сервисе
        log.info("Получение всех объявлений");
        Ads ads = new Ads();
        ads.setCount(0);
        return ResponseEntity.ok(ads);
    }

    @PostMapping(value = "/ads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(@RequestPart("properties") @Valid CreateOrUpdateAd properties,
                                    @RequestPart("image") MultipartFile image,
                                    Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Добавление объявления пользователем: {}", authentication.getName());

        Ad ad = new Ad();
        ad.setPk(1);
        ad.setAuthor(1);
        ad.setTitle(properties.getTitle());
        ad.setPrice(properties.getPrice());
        ad.setImage("/ads/1/image");

        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
    }

    @GetMapping("/ads/{id}")
    public ResponseEntity<ExtendedAd> getAd(@PathVariable Integer id) {
        // TODO: реализовать в сервисе
        log.info("Получение объявления с id: {}", id);

        ExtendedAd extendedAd = new ExtendedAd();
        extendedAd.setPk(id);
        extendedAd.setTitle("Заголовок объявления");
        extendedAd.setPrice(1000);
        extendedAd.setDescription("Описание объявления");
        extendedAd.setAuthorFirstName("Иван");
        extendedAd.setAuthorLastName("Иванов");
        extendedAd.setEmail("user@gmail.com");
        extendedAd.setPhone("+79991234567");
        extendedAd.setImage("/ads/" + id + "/image");

        return ResponseEntity.ok(extendedAd);
    }

    @DeleteMapping("/ads/{id}")
    public ResponseEntity<?> deleteAd(@PathVariable Integer id,
                                      Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Удаление объявления {} пользователем: {}", id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/ads/{id}")
    public ResponseEntity<Ad> updateAd(@PathVariable Integer id,
                                       @Valid @RequestBody CreateOrUpdateAd updateAd,
                                       Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Обновление объявления {} пользователем: {}", id, authentication.getName());

        Ad ad = new Ad();
        ad.setPk(id);
        ad.setAuthor(1);
        ad.setTitle(updateAd.getTitle());
        ad.setPrice(updateAd.getPrice());
        ad.setImage("/ads/" + id + "/image");

        return ResponseEntity.ok(ad);
    }

    @GetMapping("/ads/me")
    public ResponseEntity<Ads> getMyAds(Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Получение объявлений пользователя: {}", authentication.getName());

        Ads ads = new Ads();
        ads.setCount(0);
        return ResponseEntity.ok(ads);
    }

    @PatchMapping(value = "/ads/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateAdImage(@PathVariable Integer id,
                                                @RequestParam("image") MultipartFile image,
                                                Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Обновление изображения объявления {} пользователем: {}", id, authentication.getName());
        // Возвращаем заглушку - пустой массив байтов
        return ResponseEntity.ok(new byte[0]);
    }

    @GetMapping(value = "/ads/{id}/image", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getAdImage(@PathVariable Integer id) {
        // TODO: реализовать в сервисе
        log.info("Получение изображения объявления с id: {}", id);
        // Возвращаем заглушку - пустой массив байтов
        return ResponseEntity.ok(new byte[0]);
    }
}