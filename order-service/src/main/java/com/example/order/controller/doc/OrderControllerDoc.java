package com.example.order.controller.doc;

import com.example.order.enums.OrderStatus;
import com.example.order.dto.request.OrderRequest;
import com.example.order.dto.response.OrderResponse;
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

@Tag(name = "Orders", description = "Операции с заказами")
public interface OrderControllerDoc {

    @Operation(summary = "Создать заказ", description = "Позволить пользователю создать заказ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказ успешно создан"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка создания заказа"
            )
    })
    @PostMapping
    ResponseEntity<OrderResponse> createOrder(
            @RequestBody(
                    description = "Запрос на создание заказа",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderRequest.class)))
            OrderRequest request);

    @Operation(summary = "Получить заказ по ID", description = "Позволить пользователю получить заказ по ID")
    @GetMapping("/{id}")
    ResponseEntity<OrderResponse> getOrder(@Parameter(
            description = "ID заказа, данные по которому запрашиваются",
            required = true)
                                           @PathVariable String id);

    @Operation(summary = "Получить список заказов", description = "Позволить пользователю получить список заказов")
    @GetMapping
    ResponseEntity<List<OrderResponse>> listOrders();

    @Operation(summary = "Обновить заказ по ID", description = "Позволить пользователю обновить заказ по ID")
    @PutMapping("/{id}")
    ResponseEntity<OrderResponse> updateOrder(@PathVariable String id,
                                              @RequestBody(
                                                      description = "Запрос на обновление заказа",
                                                      required = true,
                                                      content = @Content(
                                                              mediaType = "application/json",
                                                              schema = @Schema(implementation = OrderRequest.class))) OrderRequest request,
                                              @RequestParam(value = "status", required = false) OrderStatus status);

    @Operation(summary = "Удалить заказ по ID", description = "Позволить пользователю удалить заказ по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заказ успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteOrder(@PathVariable String id);
}
