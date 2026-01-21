package ru.skypro.homework.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки OpenAPI/Swagger документации.
 * Определяет общую информацию об API и схему аутентификации.
 *
 * @author Система документации API
 * @version 1.0
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Ads Application API",
                version = "1.0",
                description = "API для управления объявлениями",
                contact = @Contact(
                        name = "Support",
                        email = "support@example.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class SpringDocConfig {
    /**
     * Конфигурация OpenAPI включает:
     * - Заголовок: "Ads Application API"
     * - Версия: "1.0"
     * - Описание: "API для управления объявлениями"
     * - Контактная информация поддержки
     * - Лицензия Apache 2.0
     * - Схема безопасности HTTP Basic Auth
     */
}