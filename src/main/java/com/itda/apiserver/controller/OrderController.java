package com.itda.apiserver.controller;

import com.itda.apiserver.annotation.LoginRequired;
import com.itda.apiserver.annotation.UserId;
import com.itda.apiserver.domain.Order;
import com.itda.apiserver.domain.OrderSheet;
import com.itda.apiserver.domain.Product;
import com.itda.apiserver.dto.*;
import com.itda.apiserver.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @LoginRequired
    @PostMapping("/api/order")
    public ApiResult<OrderResponseDto> order(@UserId Long userId, @RequestBody OrderRequestDto request) {
        List<Order> orders = orderService.order(userId, request);
        return ApiResult.ok(getOrderResponse(orders));
    }

    private OrderResponseDto getOrderResponse(List<Order> orders) {

        int totalPrice = orders.stream()
                .mapToInt(Order::getPricePerOrder)
                .sum();

        List<OrderProductResponse> orderProductResponses = orders.stream()
                .map(order -> {
                    Product product = order.getProduct();
                    OrderProductResponse response =
                            new OrderProductResponse(product.getTitle(), product.getId(), product.getPrice(), product.getDeliveryFee(),
                                    order.getQuantity(), product.getBank(), product.getAccountHolder(), product.getAccount());
                    return response;
                }).collect(Collectors.toList());

        return new OrderResponseDto(orderProductResponses, totalPrice);
    }

    @LoginRequired
    @GetMapping("/api/myPage/orders")
    public ApiResult<MyOrderResponseDto> showOrders(@UserId Long userId, @RequestParam(required = false) Integer period, Pageable pageable) {
        List<OrderSheet> orderSheet = orderService.getOrderSheet(userId, period, pageable);
        return ApiResult.ok(getMyOrderResponse(orderSheet));
    }

    private MyOrderResponseDto getMyOrderResponse(List<OrderSheet> orderSheets) {

        List<OrderSheetResponseDto> orderSheetResponseDtos = orderSheets.stream()
                .map(orderSheet -> {
                    List<MyOrder> myOrders = orderSheet.getOrders().stream()
                            .map(order -> {
                                Product product = order.getProduct();
                                return new MyOrder(product.getTitle(), product.getId(), product.getPrice(), product.getDeliveryFee(),
                                        order.getQuantity(), product.getBank(), product.getAccountHolder(), product.getAccount());
                            }).collect(Collectors.toList());
                    return new OrderSheetResponseDto(orderSheet.getId(), orderSheet.getCreatedAt().toLocalDate(), myOrders);
                }).collect(Collectors.toList());

        return new MyOrderResponseDto(orderSheetResponseDtos);
    }

}
