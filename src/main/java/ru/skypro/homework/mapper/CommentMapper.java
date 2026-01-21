package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Маппер для преобразования между сущностью комментария (CommentEntity) и DTO комментариев.
 * Обеспечивает преобразование данных между слоем базы данных и слоем представления для комментариев.
 *
 * @author Система маппинга комментариев
 * @version 1.0
 */
@Component
public class CommentMapper {

    /**
     * Преобразует сущность комментария в DTO комментария.
     * Создает объект Comment на основе данных из CommentEntity, включая информацию об авторе.
     *
     * @param entity сущность комментария из базы данных
     * @return DTO комментария для передачи клиенту
     */
    public Comment toDto(CommentEntity entity) {
        Comment comment = new Comment();
        comment.setPk(entity.getId());
        comment.setText(entity.getText());

        if (entity.getAuthor() != null) {
            comment.setAuthor(entity.getAuthor().getId());
            comment.setAuthorFirstName(entity.getAuthor().getFirstName());
            comment.setAuthorImage(entity.getAuthor().getImage());
        }

        if (entity.getCreatedAt() != null) {
            comment.setCreatedAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }

        return comment;
    }

    /**
     * Преобразует DTO для создания комментария в сущность комментария.
     * Создает новый объект CommentEntity на основе данных из CreateOrUpdateComment DTO,
     * устанавливая связи с автором и объявлением.
     *
     * @param dto DTO с данными для создания комментария
     * @param author сущность автора комментария
     * @param ad сущность объявления, к которому относится комментарий
     * @return новая сущность комментария
     */
    public CommentEntity toEntity(CreateOrUpdateComment dto, UserEntity author, AdEntity ad) {
        CommentEntity entity = new CommentEntity();
        entity.setText(dto.getText());
        entity.setAuthor(author);
        entity.setAd(ad);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    /**
     * Обновляет существующую сущность комментария данными из DTO.
     * Используется для обновления текста комментария.
     *
     * @param dto DTO с новыми данными комментария
     * @param entity сущность комментария для обновления
     */
    public void updateEntity(CreateOrUpdateComment dto, CommentEntity entity) {
        entity.setText(dto.getText());
    }
}