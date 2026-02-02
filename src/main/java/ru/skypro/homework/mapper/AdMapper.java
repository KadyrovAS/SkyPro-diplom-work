package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;

/**
 * Маппер для преобразования между сущностью объявления (AdEntity) и DTO объявлений.
 * Обеспечивает преобразование данных между слоем базы данных и слоем представления.
 *
 * @author Система маппинга объявлений
 * @version 1.0
 */
@Component
public class AdMapper {

    /**
     * Преобразует сущность объявления в DTO объявления.
     * Создает объект Ad на основе данных из AdEntity.
     *
     * @param entity сущность объявления из базы данных
     * @return DTO объявления для передачи клиенту
     */
    public Ad toDto(AdEntity entity) {
        Ad ad = new Ad();
        ad.setPk(entity.getId());
        ad.setAuthor(entity.getAuthor().getId());
        ad.setTitle(entity.getTitle());
        ad.setPrice(entity.getPrice());
        // Изменено: возвращаем URL для получения изображения через контроллер
        if (entity.getImage() != null && !entity.getImage().isEmpty()) {
            ad.setImage("/ads/" + entity.getId() + "/image");
        } else {
            ad.setImage("");
        }
        return ad;
    }

    /**
     * Преобразует сущность объявления в расширенное DTO объявления.
     * Создает объект ExtendedAd с полной информацией об объявлении, включая данные автора.
     *
     * @param entity сущность объявления из базы данных
     * @return расширенное DTO объявления с дополнительной информацией
     */
    public ExtendedAd toExtendedAd(AdEntity entity) {
        ExtendedAd extendedAd = new ExtendedAd();
        extendedAd.setPk(entity.getId());
        extendedAd.setTitle(entity.getTitle());
        extendedAd.setPrice(entity.getPrice());
        extendedAd.setDescription(entity.getDescription());

        // Изменено: возвращаем URL для получения изображения через контроллер
        if (entity.getImage() != null && !entity.getImage().isEmpty()) {
            extendedAd.setImage("/ads/" + entity.getId() + "/image");
        } else {
            extendedAd.setImage("");
        }

        if (entity.getAuthor() != null) {
            extendedAd.setAuthorFirstName(entity.getAuthor().getFirstName());
            extendedAd.setAuthorLastName(entity.getAuthor().getLastName());
            extendedAd.setEmail(entity.getAuthor().getEmail());
            extendedAd.setPhone(entity.getAuthor().getPhone());
        }

        return extendedAd;
    }

    /**
     * Преобразует DTO для создания/обновления объявления в сущность объявления.
     * Создает новый объект AdEntity на основе данных из CreateOrUpdateAd DTO.
     *
     * @param dto DTO с данными для создания или обновления объявления
     * @return новая сущность объявления
     */
    public AdEntity toEntity(CreateOrUpdateAd dto) {
        AdEntity entity = new AdEntity();
        updateEntity(dto, entity);
        return entity;
    }

    /**
     * Обновляет существующую сущность объявления данными из DTO.
     * Используется для обновления полей сущности без создания нового объекта.
     *
     * @param dto DTO с новыми данными объявления
     * @param entity сущность объявления для обновления
     */
    public void updateEntity(CreateOrUpdateAd dto, AdEntity entity) {
        entity.setTitle(dto.getTitle());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
    }
}