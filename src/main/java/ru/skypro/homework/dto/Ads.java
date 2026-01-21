package ru.skypro.homework.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO (Data Transfer Object) для представления списка объявлений с пагинацией.
 * Содержит общее количество объявлений и список самих объявлений.
 *
 * @author DTO списка объявлений
 * @version 1.0
 */
@Data
public class Ads {
    /** Общее количество объявлений */
    private Integer count;

    /** Список объявлений */
    private List<Ad> results;
}