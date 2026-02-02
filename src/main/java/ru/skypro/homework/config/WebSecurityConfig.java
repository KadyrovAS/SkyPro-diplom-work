package ru.skypro.homework.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.skypro.homework.service.impl.CustomUserDetailsManager;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Конфигурационный класс безопасности приложения.
 * Настраивает аутентификацию, авторизацию, CORS и параметры безопасности для HTTP запросов.
 * Использует CustomUserDetailsManager для работы с пользователями в базе данных.
 *
 * @author Система безопасности
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    /**
     * Список URL, доступных без аутентификации.
     * Включает эндпоинты Swagger, OpenAPI, публичные API и ресурсы.
     */
    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/webjars/**",
            "/login",
            "/register",
            "/ads",
            "/ads/*",
            "/ads/*/image",
            "/ads/*/comments",
            "/users/*/image"
    };

    /**
     * Конфигурирует цепочку фильтров безопасности.
     * Настраивает CORS, отключает CSRF, определяет правила авторизации и включает HTTP Basic аутентификацию.
     * Использует CustomUserDetailsManager для аутентификации пользователей.
     *
     * @param http объект HttpSecurity для настройки
     * @return сконфигурированная цепочка фильтров безопасности
     * @throws Exception если произошла ошибка при конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf().disable()
                .authorizeHttpRequests(authorization ->
                        authorization
                                .mvcMatchers(AUTH_WHITELIST).permitAll()
                                .mvcMatchers("/ads/**", "/users/**").authenticated())
                .httpBasic(withDefaults());
        return http.build();
    }

    /**
     * Создает менеджер пользователей для работы с базой данных.
     * Реализует интерфейс UserDetailsManager для управления пользователями в Spring Security.
     *
     * @return реализация UserDetailsManager на основе JPA
     */
    @Bean
    public UserDetailsManager userDetailsManager(CustomUserDetailsManager customUserDetailsManager) {
        return customUserDetailsManager;
    }

    /**
     * Создает источник конфигурации CORS (Cross-Origin Resource Sharing).
     * Разрешает запросы с фронтенда на localhost:3000 с указанными методами и заголовками.
     *
     * @return источник конфигурации CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Создает кодировщик паролей для безопасного хранения в базе данных.
     * Использует алгоритм BCrypt с силой хэширования по умолчанию.
     *
     * @return реализация PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}