package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.BadRequestException;
import ru.skypro.homework.exception.ForbiddenException;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с комментариями к объявлениям.
 * Обеспечивает создание, получение, обновление и удаление комментариев.
 * Реализует проверку прав доступа для операций с комментариями.
 *
 * @author Система управления комментариями
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    /**
     * Получает все комментарии для указанного объявления.
     *
     * @param adId идентификатор объявления
     * @return {@link Comments} объект с количеством и списком комментариев
     * @throws NotFoundException если объявление не найдено
     */
    @Override
    public Comments getComments(Integer adId) {
        log.debug("Получение комментариев для объявления ID: {}", adId);

        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + adId));

        List<CommentEntity> commentEntities = commentRepository.findByAd(adEntity);
        List<Comment> comments = commentEntities.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        Comments result = new Comments();
        result.setCount(comments.size());
        result.setResults(comments);

        log.info("Получено {} комментариев для объявления ID: {}", comments.size(), adId);
        return result;
    }

    /**
     * Добавляет новый комментарий к объявлению.
     * Автоматически устанавливает текущего пользователя как автора комментария.
     *
     * @param adId         идентификатор объявления
     * @param comment      DTO с текстом комментария
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link Comment} DTO созданного комментария
     * @throws NotFoundException если объявление или пользователь не найдены
     * @throws BadRequestException если текст комментария не прошел валидацию
     */
    @Override
    public Comment addComment(Integer adId, CreateOrUpdateComment comment, Authentication authentication) {
        log.debug("Добавление комментария к объявлению ID: {}", adId);

        // Валидация входных данных
        if (comment.getText() == null || comment.getText().trim().isEmpty()) {
            throw new BadRequestException("Текст комментария не может быть пустым");
        }

        if (comment.getText().length() < 8 || comment.getText().length() > 64) {
            throw new BadRequestException("Текст комментария должен быть от 8 до 64 символов");
        }

        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("Объявление не найдено с ID: " + adId));

        UserEntity author = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        CommentEntity commentEntity = commentMapper.toEntity(comment, author, adEntity);
        CommentEntity savedComment = commentRepository.save(commentEntity);

        log.info("Добавлен комментарий ID: {} к объявлению ID: {}, автор: {}",
                savedComment.getId(), adId, author.getEmail());

        return commentMapper.toDto(savedComment);
    }

    /**
     * Удаляет комментарий по его идентификатору.
     * Проверяет, что комментарий принадлежит указанному объявлению.
     * Проверяет права доступа: только автор комментария или администратор может удалить комментарий.
     *
     * @param adId         идентификатор объявления
     * @param commentId    идентификатор комментария
     * @param authentication объект аутентификации текущего пользователя
     * @throws NotFoundException если комментарий или пользователь не найдены
     * @throws ForbiddenException если у пользователя нет прав на удаление
     */
    @Override
    public void deleteComment(Integer adId, Integer commentId, Authentication authentication) {
        log.debug("Удаление комментария ID: {} к объявлению ID: {}", commentId, adId);

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден с ID: " + commentId));

        // Проверяем, что комментарий относится к указанному объявлению
        if (!commentEntity.getAd().getId().equals(adId)) {
            throw new NotFoundException("Комментарий ID: " + commentId + " не принадлежит объявлению ID: " + adId);
        }

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        // Проверка прав: автор комментария или админ
        if (!commentEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Нет прав на удаление комментария. Только автор или администратор могут удалить комментарий.");
        }

        commentRepository.delete(commentEntity);
        log.info("Удален комментарий ID: {} к объявлению ID: {}", commentId, adId);
    }

    /**
     * Обновляет существующий комментарий.
     * Проверяет, что комментарий принадлежит указанному объявлению.
     * Проверяет права доступа: только автор комментария или администратор может редактировать комментарий.
     *
     * @param adId         идентификатор объявления
     * @param commentId    идентификатор комментария
     * @param comment      DTO с новым текстом комментария
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link Comment} DTO обновленного комментария
     * @throws NotFoundException если комментарий или пользователь не найдены
     * @throws ForbiddenException если у пользователя нет прав на редактирование
     * @throws BadRequestException если текст комментария не прошел валидацию
     */
    @Override
    public Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment comment, Authentication authentication) {
        log.debug("Обновление комментария ID: {} к объявлению ID: {}", commentId, adId);

        // Валидация входных данных
        if (comment.getText() == null || comment.getText().trim().isEmpty()) {
            throw new BadRequestException("Текст комментария не может быть пустым");
        }

        if (comment.getText().length() < 8 || comment.getText().length() > 64) {
            throw new BadRequestException("Текст комментария должен быть от 8 до 64 символов");
        }

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден с ID: " + commentId));

        // Проверяем, что комментарий относится к указанному объявлению
        if (!commentEntity.getAd().getId().equals(adId)) {
            throw new NotFoundException("Комментарий ID: " + commentId + " не принадлежит объявлению ID: " + adId);
        }

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));

        // Проверка прав: автор комментария или админ
        if (!commentEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Нет прав на редактирование комментария. Только автор или администратор могут редактировать комментарий.");
        }

        commentMapper.updateEntity(comment, commentEntity);
        CommentEntity updatedComment = commentRepository.save(commentEntity);

        log.info("Обновлен комментарий ID: {} к объявлению ID: {}", commentId, adId);
        return commentMapper.toDto(updatedComment);
    }
}