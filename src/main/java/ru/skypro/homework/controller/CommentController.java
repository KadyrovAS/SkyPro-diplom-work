package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

import javax.validation.Valid;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class CommentController {

    @GetMapping("/ads/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable Integer id) {
        // TODO: реализовать в сервисе
        log.info("Получение комментариев для объявления: {}", id);

        Comments comments = new Comments();
        comments.setCount(0);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/ads/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Integer id,
                                              @Valid @RequestBody CreateOrUpdateComment comment,
                                              Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Добавление комментария к объявлению {} пользователем: {}", id, authentication.getName());

        Comment newComment = new Comment();
        newComment.setPk(1);
        newComment.setAuthor(1);
        newComment.setAuthorFirstName("Иван");
        newComment.setAuthorImage("/users/1/image");
        newComment.setCreatedAt(System.currentTimeMillis());
        newComment.setText(comment.getText());

        return ResponseEntity.ok(newComment);
    }

    @DeleteMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer adId,
                                           @PathVariable Integer commentId,
                                           Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Удаление комментария {} объявления {} пользователем: {}",
                commentId, adId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @Valid @RequestBody CreateOrUpdateComment comment,
                                                 Authentication authentication) {
        // TODO: реализовать в сервисе
        log.info("Обновление комментария {} объявления {} пользователем: {}",
                commentId, adId, authentication.getName());

        Comment updatedComment = new Comment();
        updatedComment.setPk(commentId);
        updatedComment.setAuthor(1);
        updatedComment.setAuthorFirstName("Иван");
        updatedComment.setAuthorImage("/users/1/image");
        updatedComment.setCreatedAt(System.currentTimeMillis());
        updatedComment.setText(comment.getText());

        return ResponseEntity.ok(updatedComment);
    }
}