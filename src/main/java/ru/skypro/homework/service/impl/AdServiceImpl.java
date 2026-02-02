package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.BadRequestException;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с объявлениями.
 * Реализует бизнес-логику управления объявлениями, включая создание,
 * получение, обновление и удаление объявлений, а также работу с изображениями.
 *
 * @author Система управления объявлениями
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final FileService fileService;

    /**
     * Получает список всех объявлений.
     * Возвращает объект, содержащий общее количество объявлений и список DTO объявлений.
     *
     * @return {@link Ads} объект с количеством и списком объявлений
     */
    @Override
    public Ads getAllAds() {
        List<AdEntity> adEntities = adRepository.findAll();
        List<Ad> ads = adEntities.stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());

        Ads result = new Ads();
        result.setCount(ads.size());
        result.setResults(ads);

        log.info("Получены все объявления, количество: {}", ads.size());
        return result;
    }

    /**
     * Создает новое объявление.
     * Сохраняет переданные свойства объявления и изображение, связывая их с текущим пользователем.
     *
     * @param properties   данные для создания объявления
     * @param image        файл изображения объявления
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link Ad} DTO созданного объявления
     * @throws NotFoundException   если пользователь не найден
     * @throws BadRequestException если изображение не предоставлено или произошла ошибка при сохранении
     */
    @Override
    public Ad addAd(CreateOrUpdateAd properties, MultipartFile image, Authentication authentication) {
        UserEntity author = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        AdEntity adEntity = adMapper.toEntity(properties);
        adEntity.setAuthor(author);

        // Сохраняем изображение
        if (image != null && !image.isEmpty()) {
            try {
                // Изменено: сохраняем только имя файла
                String imageFilename = fileService.saveImage(image, "ads");
                adEntity.setImage(imageFilename);
            } catch (IOException e) {
                throw new BadRequestException("Ошибка при сохранении изображения: " + e.getMessage());
            }
        } else {
            throw new BadRequestException("Изображение объявления обязательно");
        }

        AdEntity savedAd = adRepository.save(adEntity);
        log.info("Добавлено новое объявление ID: {}, автор: {}", savedAd.getId(), author.getEmail());

        return adMapper.toDto(savedAd);
    }

    /**
     * Получает полную информацию об объявлении по его идентификатору.
     * Возвращает расширенную информацию об объявлении, включая данные автора.
     *
     * @param id идентификатор объявления
     * @return {@link ExtendedAd} расширенная информация об объявлении
     * @throws NotFoundException если объявление с указанным ID не найдено
     */
    @Override
    public ExtendedAd getAd(Integer id) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        log.debug("Получено объявление ID: {}", id);
        return adMapper.toExtendedAd(adEntity);
    }

    /**
     * Удаляет объявление по его идентификатору.
     * Проверяет права доступа: только автор или администратор может удалить объявление.
     * Удаляет связанное изображение из файловой системы.
     *
     * @param id              идентификатор объявления
     * @param authentication объект аутентификации текущего пользователя
     * @throws NotFoundException   если объявление или пользователь не найдены
     * @throws ForbiddenException  если у пользователя нет прав на удаление
     */
    @Override
    public void deleteAd(Integer id, Authentication authentication) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        // Проверяем права
        if (!adEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Нет прав на удаление объявления");
        }

        // Удаляем изображение
        if (adEntity.getImage() != null) {
            try {
                fileService.deleteImage("ads", adEntity.getImage());
            } catch (IOException e) {
                log.error("Ошибка при удалении изображения объявления {}: {}", id, e.getMessage());
            }
        }

        adRepository.delete(adEntity);
        log.info("Удалено объявление ID: {}", id);
    }

    /**
     * Обновляет информацию об объявлении.
     * Проверяет права доступа: только автор или администратор может редактировать объявление.
     *
     * @param id              идентификатор объявления
     * @param updateAd        новые данные для обновления
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link Ad} DTO обновленного объявления
     * @throws NotFoundException   если объявление или пользователь не найдены
     * @throws ForbiddenException  если у пользователя нет прав на редактирование
     */
    @Override
    public Ad updateAd(Integer id, CreateOrUpdateAd updateAd, Authentication authentication) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        // Проверяем права
        if (!adEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Нет прав на редактирование объявления");
        }

        // Обновляем поля
        adMapper.updateEntity(updateAd, adEntity);
        AdEntity updatedAd = adRepository.save(adEntity);

        log.info("Обновлено объявление ID: {}", id);
        return adMapper.toDto(updatedAd);
    }

    /**
     * Получает список объявлений текущего пользователя.
     *
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link Ads} объект с количеством и списком объявлений пользователя
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    public Ads getMyAds(Authentication authentication) {
        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        List<AdEntity> adEntities = adRepository.findByAuthor(currentUser);
        List<Ad> ads = adEntities.stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());

        Ads result = new Ads();
        result.setCount(ads.size());
        result.setResults(ads);

        log.info("Получены объявления пользователя {}, количество: {}", currentUser.getEmail(), ads.size());
        return result;
    }

    /**
     * Обновляет изображение объявления.
     * Удаляет старое изображение и сохраняет новое в файловой системе.
     *
     * @param id              идентификатор объявления
     * @param image           новый файл изображения
     * @param authentication объект аутентификации текущего пользователя
     * @throws NotFoundException   если объявление или пользователь не найдены
     * @throws BadRequestException если файл изображения отсутствует или пуст
     * @throws ForbiddenException  если у пользователя нет прав на редактирование
     */
    @Override
    public void updateAdImage(Integer id, MultipartFile image, Authentication authentication) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        // Проверяем права
        if (!adEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Нет прав на редактирование объявления");
        }

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Файл изображения отсутствует или пуст");
        }

        // Удаляем старое изображение
        if (adEntity.getImage() != null) {
            try {
                fileService.deleteImage("ads", adEntity.getImage());
            } catch (IOException e) {
                log.error("Ошибка при удалении старого изображения: {}", e.getMessage());
            }
        }

        // Сохраняем новое изображение
        try {
            // Изменено: сохраняем только имя файла
            String imageFilename = fileService.saveImage(image, "ads");
            adEntity.setImage(imageFilename);
            adRepository.save(adEntity);
            log.info("Обновлено изображение объявления ID: {}", id);
        } catch (IOException e) {
            throw new BadRequestException("Ошибка при сохранении изображения: " + e.getMessage());
        }
    }

    /**
     * Получает изображение объявления в виде массива байтов.
     *
     * @param id идентификатор объявления
     * @return массив байтов изображения или пустой массив, если изображение не найдено
     */
    @Override
    public byte[] getAdImage(Integer id) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        if (adEntity.getImage() == null || adEntity.getImage().isEmpty()) {
            log.warn("Изображение для объявления {} не найдено", id);
            return new byte[0];
        }

        try {
            // Изменено: загружаем изображение с указанием поддиректории
            return fileService.loadImage("ads", adEntity.getImage());
        } catch (IOException e) {
            log.error("Ошибка при чтении изображения объявления {}: {}", id, e.getMessage());
            return new byte[0];
        }
    }
}