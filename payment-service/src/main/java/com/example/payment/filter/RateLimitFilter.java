package com.example.payment.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    // Хранилище корзин для разных клиентов (по IP)
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        // Приводим к HTTP-типам
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Идентификация клиента (можно по IP или по API-ключу в заголовке)
        String clientIp = httpRequest.getRemoteAddr();

        // Получаем бакет для конкретного IP или создаем новый
        Bucket bucket = cache.computeIfAbsent(clientIp, k -> createNewBucket());

        // Проверяем наличие токенов
        if (bucket.tryConsume(1)) {
            // Если токен есть — пропускаем запрос дальше по цепочке
            chain.doFilter(request, response);
        } else {
            // Если лимит исчерпан — возвращаем 429 Too Many Requests
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Too many requests\"}");
        }

    }

    // Настройка лимита: 100 запросов в секунду
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth
                .builder()
                .capacity(5)   // Максимальное число токенов (разрешенных запросов)
                .refillIntervally(5, Duration.ofMinutes(1))    // Пополнение бакета 100-ми токенами
                .build();                                               // каждую 1 минуту

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

}
