package ru.skypro.homework.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CreateOrUpdateAd {
    @Size(min = 4, max = 32)
    private String title;
    @Min(0)
    @Max(10000000)
    private Integer price;
    @Size(min = 8, max = 64)
    private String description;
}