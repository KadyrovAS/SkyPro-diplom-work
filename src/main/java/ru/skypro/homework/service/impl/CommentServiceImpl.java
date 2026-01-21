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
     * @throws RuntimeException если объявление не найдено
     */
    @Override
    public Comments getComments(Integer adId) {
        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        List<CommentEntity> commentEntities = commentRepository.findByAd(adEntity);
        List<Comment> comments = commentEntities.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        Comments result = new Comments();
        result.setCount(comments.size());
        result.setResults(comments);
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
     * @throws RuntimeException если объявление или пользователь не найдены
     */
    @Override
    public Comment addComment(Integer adId, CreateOrUpdateComment comment, Authentication authentication) {
        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        UserEntity author = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        CommentEntity commentEntity = commentMapper.toEntity(comment, author, adEntity);
        CommentEntity savedComment = commentRepository.save(commentEntity);

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
     * @throws RuntimeException если комментарий не найден, не принадлежит объявлению или нет прав доступа
     */
    @Override
    public void deleteComment(Integer adId, Integer commentId, Authentication authentication) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        // Проверяем, что комментарий относится к указанному объявлению
        if (!commentEntity.getAd().getId().equals(adId)) {
            throw new RuntimeException("Комментарий не принадлежит данному объявлению");
        }

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверка прав: автор комментария или админ
        if (!commentEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Нет прав на удаление комментария");
        }

        commentRepository.delete(commentEntity);
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
     * @throws RuntimeException если комментарий не найден, не принадлежит объявлению или нет прав доступа
     */
    @Override
    public Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment comment, Authentication authentication) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        // Проверяем, что комментарий относится к указанному объявлению
        if (!commentEntity.getAd().getId().equals(adId)) {
            throw new RuntimeException("Комментарий не принадлежит данному объявлению");
        }

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверка прав: автор комментария или админ
        if (!commentEntity.getAuthor().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Нет прав на редактирование комментария");
        }

        commentMapper.updateEntity(comment, commentEntity);
        CommentEntity updatedComment = commentRepository.save(commentEntity);

        return commentMapper.toDto(updatedComment);
    }
}