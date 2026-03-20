package com.example.payment.controller.doc;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payments", description = "Операции с оплатой")
public interface PaymentControllerDoc {

    @Operation(summary = "Создать платеж", description = "Позволить пользователю создать платеж")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Платеж успешно создан"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка создания платежа"
            )
    })
    @PostMapping
    ResponseEntity<PaymentResponse> createPayment(
            @RequestBody(
                    description = "Запрос на создание платежа",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentRequest.class)))
            PaymentRequest request);

    @Operation(summary = "Получить платеж по ID", description = "Позволить пользователю получить платеж по ID")
    @GetMapping("/{id}")
    ResponseEntity<PaymentResponse> getPayment(@Parameter(
            description = "ID заказа, данные по которому запрашиваются",
            required = true)
                                           @PathVariable String id);

    @Operation(summary = "Получить список платежей", description = "Позволить пользователю получить список платежей")
    @GetMapping
    ResponseEntity<List<PaymentResponse>> listPayments();

    @Operation(summary = "Обновить платеж по ID", description = "Позволить пользователю обновить платеж по ID")
    @PutMapping("/{id}")
    ResponseEntity<PaymentResponse> updatePayment(@PathVariable String id,
                                              @RequestBody(
                                                      description = "Запрос на обновление платежа",
                                                      required = true,
                                                      content = @Content(
                                                              mediaType = "application/json",
                                                              schema = @Schema(implementation = PaymentRequest.class)))
                                              PaymentRequest request);

    @Operation(summary = "Удалить платеж по ID", description = "Позволить пользователю удалить платеж по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Платеж успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Платеж не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePayment(@PathVariable String id);
}
