package com.example.payment.interceptor;

import com.example.payment.domain.IdempotencyKey;
import com.example.payment.enums.IdempotencyKeyStatus;
import com.example.payment.exception.IdempotencyKeyExistsException;
import com.example.payment.service.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import static com.example.payment.constant.WebConstants.WRAPPED_RESPONSE_ATTRIBUTE_NAME;

@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private static final String IDEMPOTENT_KEY_HEADER_NAME = "X-Idempotency-Key";

    private final IdempotencyService idempotencyService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        var method = HttpMethod.valueOf(request.getMethod());

        if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PATCH)) {
            var idempotencyKey = request.getHeader(IDEMPOTENT_KEY_HEADER_NAME);
            if (StringUtils.isBlank(idempotencyKey)) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().println("X-Idempotency-Key is not present");
                return false;
            }

            return processIdempotency(idempotencyKey, response);
        }

        return true;
    }

    private boolean processIdempotency(String idempotencyKey,
                                       HttpServletResponse response) throws IOException {

        var existingKey = idempotencyService.getByKey(idempotencyKey);

        if (existingKey.isPresent()) {
            return processExistingKey(existingKey.get(), response);
        } else {
            return createNewKey(idempotencyKey, response);
        }
    }

    private boolean processExistingKey(IdempotencyKey idempotencyKey,
                                       HttpServletResponse response) throws IOException {
        var status = idempotencyKey.getStatus();

        if (status == IdempotencyKeyStatus.PENDING) {
            response.setStatus(HttpStatus.CONFLICT.value());
            response.getWriter().println("Same request is already in progress...");
        } else if (status == IdempotencyKeyStatus.COMPLETED) {
            response.setStatus(idempotencyKey.getStatusCode());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(idempotencyKey.getResponse());
        } else {
            throw new IllegalArgumentException("Invalid status of idempotency key");
        }

        return false;
    }

    private boolean createNewKey(String idempotencyKey,
                                 HttpServletResponse response) throws IOException {
        try {
            idempotencyService.createPendingKey(idempotencyKey);
            return true;
        } catch (IdempotencyKeyExistsException e) {
            response.setStatus(HttpStatus.CONFLICT.value());
            response.getWriter().println("Same request is already in progress...");
            return false;
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) throws Exception {
        var method = HttpMethod.valueOf(request.getMethod());

        if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PATCH)) {
            // Обертка необходима для возможности повторных чтений response-а
            var wrappedResponse = (ContentCachingResponseWrapper) request.getAttribute(WRAPPED_RESPONSE_ATTRIBUTE_NAME);
            var responseBody = new String(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding());

            var idempotencyKey = request.getHeader(IDEMPOTENT_KEY_HEADER_NAME);
            idempotencyService.markAsCompleted(idempotencyKey, responseBody, response.getStatus());
        }
    }
}
