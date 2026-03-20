package com.example.delivery.controller.doc;

import com.example.delivery.dto.DeliveryRequest;
import com.example.delivery.dto.DeliveryResponse;
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

@Tag(name = "Delivery", description = "Операции с доставкой")
public interface DeliveryControllerDoc {

    @Operation(summary = "Создать доставку", description = "Позволить пользователю создать доставку")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Доставка успешно создана"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка создания доставки"
            )
    })
    @PostMapping
    ResponseEntity<DeliveryResponse> createDelivery(
            @RequestBody(
                    description = "Запрос на создание доставки",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryRequest.class)))
            DeliveryRequest request);

    @Operation(summary = "Получить доставку по ID", description = "Позволить пользователю получить доставку по ID")
    @GetMapping("/{id}")
    ResponseEntity<DeliveryResponse> getDelivery(@Parameter(
            description = "ID доставки, данные по которой запрашиваются",
            required = true)
                                           @PathVariable String id);

    @Operation(summary = "Получить список доставок", description = "Позволить пользователю получить список доставок")
    @GetMapping
    ResponseEntity<List<DeliveryResponse>> listDelivers();

    @Operation(summary = "Обновить доставку по ID", description = "Позволить пользователю обновить доставку по ID")
    @PutMapping("/{id}")
    ResponseEntity<DeliveryResponse> updateDelivery(@PathVariable String id,
                                                    @RequestBody(
                                                      description = "Запрос на обновление доставки",
                                                      required = true,
                                                      content = @Content(
                                                              mediaType = "application/json",
                                                              schema = @Schema(implementation = DeliveryRequest.class)))
                                              DeliveryRequest request);

    @Operation(summary = "Удалить доставку по ID", description = "Позволить пользователю удалить доставку по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Доставка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Доставка не найдена")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDelivery(@PathVariable String id);
}
