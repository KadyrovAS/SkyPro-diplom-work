package ru.skypro.homework.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO (Data Transfer Object) для представления списка комментариев с пагинацией.
 * Содержит общее количество комментариев и список самих комментариев.
 *
 * @author DTO списка комментариев
 * @version 1.0
 */
@Data
public class Comments {
    /** Общее количество комментариев */
    private Integer count;

    /** Список комментариев */
    private List<Comment> results;
}