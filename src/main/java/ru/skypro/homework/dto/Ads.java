package ru.skypro.homework.dto;

import lombok.Data;
import java.util.List;

@Data
public class Ads {
    private Integer count;      // общее количество объявлений
    private List<Ad> results;   // список объявлений
}