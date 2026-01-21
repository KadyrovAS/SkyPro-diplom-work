package ru.skypro.homework.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр для добавления заголовков CORS, необходимых для HTTP Basic аутентификации.
 * Гарантирует, что заголовок Access-Control-Allow-Credentials установлен в true,
 * что позволяет браузеру отправлять учетные данные при кросс-доменных запросах.
 *
 * @author Фильтр CORS для аутентификации
 * @version 1.0
 */
@Component
public class BasicAuthCorsFilter extends OncePerRequestFilter {

    /**
     * Обрабатывает каждый HTTP запрос, добавляя заголовки CORS.
     * Устанавливает заголовок Access-Control-Allow-Credentials: true,
     * что необходимо для работы HTTP Basic аутентификации в кросс-доменных запросах.
     *
     * @param httpServletRequest  HTTP запрос
     * @param httpServletResponse HTTP ответ
     * @param filterChain         цепочка фильтров
     * @throws ServletException если произошла ошибка сервлета
     * @throws IOException      если произошла ошибка ввода/вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}