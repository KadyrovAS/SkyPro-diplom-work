package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

import javax.validation.Valid;

/**
 * Контроллер для управления комментариями к объявлениям.
 * Обрабатывает запросы на создание, получение, обновление и удаление комментариев.
 *
 * @author Контроллер комментариев
 * @version 1.0
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Получает все комментарии для указанного объявления.
     *
     * @param id идентификатор объявления
     * @return ResponseEntity с объектом Comments, содержащим список комментариев
     */
    @GetMapping("/ads/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable Integer id) {
        Comments comments = commentService.getComments(id);
        return ResponseEntity.ok(comments);
    }

    /**
     * Добавляет новый комментарий к объявлению.
     * Автор комментария определяется по текущему аутентифицированному пользователю.
     *
     * @param id идентификатор объявления
     * @param comment DTO с текстом комментария
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity с созданным комментарием
     */
    @PostMapping("/ads/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Integer id,
                                              @Valid @RequestBody CreateOrUpdateComment comment,
                                              Authentication authentication) {
        Comment newComment = commentService.addComment(id, comment, authentication);
        return ResponseEntity.ok(newComment);
    }

    /**
     * Удаляет комментарий по его идентификатору.
     * Только автор комментария или администратор могут удалить комментарий.
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity со статусом 200 (OK)
     */
    @DeleteMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer adId,
                                           @PathVariable Integer commentId,
                                           Authentication authentication) {
        commentService.deleteComment(adId, commentId, authentication);
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет комментарий по его идентификатору.
     * Только автор комментария или администратор могут обновить комментарий.
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param comment DTO с новым текстом комментария
     * @param authentication объект аутентификации текущего пользователя
     * @return ResponseEntity с обновленным комментарием
     */
    @PatchMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @Valid @RequestBody CreateOrUpdateComment comment,
                                                 Authentication authentication) {
        Comment updatedComment = commentService.updateComment(adId, commentId, comment, authentication);
        return ResponseEntity.ok(updatedComment);
    }
}