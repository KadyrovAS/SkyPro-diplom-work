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
import ru.skypro.homework.service.impl.FileService;

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
        log.debug("Получение всех объявлений");

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
        log.debug("Создание нового объявления пользователем: {}", authentication.getName());

        // Валидация входных данных
        if (properties.getPrice() < 0) {
            throw new BadRequestException("Цена не может быть отрицательной");
        }

        if (properties.getTitle() == null || properties.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Заголовок не может быть пустым");
        }

        if (properties.getTitle().length() < 4 || properties.getTitle().length() > 32) {
            throw new BadRequestException("Заголовок должен быть от 4 до 32 символов");
        }

        if (properties.getDescription() == null || properties.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Описание не может быть пустым");
        }

        if (properties.getDescription().length() < 8 || properties.getDescription().length() > 64) {
            throw new BadRequestException("Описание должно быть от 8 до 64 символов");
        }

        UserEntity author = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        AdEntity adEntity = adMapper.toEntity(properties);
        adEntity.setAuthor(author);

        // Сохраняем изображение
        if (image != null && !image.isEmpty()) {
            try {
                String imagePath = fileService.saveImage(image, "ads");
                adEntity.setImage(imagePath);
            } catch (IOException e) {
                throw new BadRequestException("Ошибка при сохранении изображения: " + e.getMessage());
            }
        } else {
            throw new BadRequestException("Изображение объявления обязательно");
        }

        AdEntity savedAd = adRepository.save(adEntity);
        log.info("Добавлено новое объявление ID: {}, автор: {}, заголовок: {}",
                savedAd.getId(), author.getEmail(), savedAd.getTitle());

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
        log.debug("Получение объявления ID: {}", id);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        log.info("Получено объявление ID: {}, заголовок: {}", id, adEntity.getTitle());
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
        log.debug("Удаление объявления ID: {}", id);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        // Проверяем права
        if (!adEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Нет прав на удаление объявления. Только автор или администратор могут удалить объявление.");
        }

        // Удаляем изображение
        if (adEntity.getImage() != null) {
            try {
                fileService.deleteImage(adEntity.getImage());
            } catch (IOException e) {
                log.error("Ошибка при удалении изображения объявления {}: {}", id, e.getMessage());
            }
        }

        adRepository.delete(adEntity);
        log.info("Удалено объявление ID: {}, заголовок: {}", id, adEntity.getTitle());
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
        log.debug("Обновление объявления ID: {}", id);

        // Валидация входных данных
        if (updateAd.getPrice() != null && updateAd.getPrice() < 0) {
            throw new BadRequestException("Цена не может быть отрицательной");
        }

        if (updateAd.getTitle() != null && updateAd.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Заголовок не может быть пустым");
        }

        if (updateAd.getTitle() != null && (updateAd.getTitle().length() < 4 || updateAd.getTitle().length() > 32)) {
            throw new BadRequestException("Заголовок должен быть от 4 до 32 символов");
        }

        if (updateAd.getDescription() != null && updateAd.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Описание не может быть пустым");
        }

        if (updateAd.getDescription() != null && (updateAd.getDescription().length() < 8 || updateAd.getDescription().length() > 64)) {
            throw new BadRequestException("Описание должно быть от 8 до 64 символов");
        }

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        // Проверяем права
        if (!adEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Нет прав на редактирование объявления. Только автор или администратор могут редактировать объявление.");
        }

        // Обновляем поля
        adMapper.updateEntity(updateAd, adEntity);
        AdEntity updatedAd = adRepository.save(adEntity);

        log.info("Обновлено объявление ID: {}, заголовок: {}", id, updatedAd.getTitle());
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
        log.debug("Получение объявлений пользователя: {}", authentication.getName());

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
        log.debug("Обновление изображения объявления ID: {}", id);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        // Проверяем права
        if (!adEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Нет прав на редактирование объявления. Только автор или администратор могут обновить изображение.");
        }

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Файл изображения отсутствует или пуст");
        }

        // Проверяем тип файла
        String contentType = image.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") && !contentType.equals("image/png") &&
                        !contentType.equals("image/jpg"))) {
            throw new BadRequestException("Разрешены только изображения в формате JPEG, JPG или PNG");
        }

        // Проверяем размер файла (10MB)
        if (image.getSize() > 10 * 1024 * 1024) {
            throw new BadRequestException("Размер файла не должен превышать 10MB");
        }

        // Удаляем старое изображение
        if (adEntity.getImage() != null) {
            try {
                fileService.deleteImage(adEntity.getImage());
            } catch (IOException e) {
                log.error("Ошибка при удалении старого изображения: {}", e.getMessage());
            }
        }

        // Сохраняем новое изображение
        try {
            String imagePath = fileService.saveImage(image, "ads");
            adEntity.setImage(imagePath);
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
     * @throws NotFoundException если объявление не найдено
     */
    @Override
    public byte[] getAdImage(Integer id) {
        log.debug("Получение изображения объявления ID: {}", id);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + id));

        if (adEntity.getImage() == null || adEntity.getImage().isEmpty()) {
            log.warn("Изображение для объявления {} не найдено", id);
            return new byte[0];
        }

        try {
            byte[] imageData = fileService.loadImage(adEntity.getImage());
            log.info("Получено изображение объявления ID: {}, размер: {} байт", id, imageData.length);
            return imageData;
        } catch (IOException e) {
            log.error("Ошибка при чтении изображения объявления {}: {}", id, e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Проверяет, является ли текущий пользователь автором объявления.
     * Используется в аннотациях @PreAuthorize для проверки прав доступа.
     *
     * @param adId идентификатор объявления
     * @param authentication объект аутентификации текущего пользователя
     * @return true если пользователь является автором объявления, false в противном случае
     */
    public boolean isAdAuthor(Integer adId, Authentication authentication) {
        log.debug("Проверка прав авторства для объявления ID: {}", adId);

        try {
            AdEntity adEntity = adRepository.findById(adId)
                    .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + adId));

            UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

            boolean isAuthor = adEntity.getAuthor().getId().equals(currentUser.getId());
            log.debug("Пользователь {} является автором объявления ID: {}: {}",
                    authentication.getName(), adId, isAuthor);

            return isAuthor;
        } catch (NotFoundException e) {
            log.warn("Ошибка при проверке прав авторства: {}", e.getMessage());
            return false;
        }
    }
}