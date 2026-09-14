package com.rbdip.bookstore.order;

import org.springframework.stereotype.Component;

/**
 * Валидация входящего запроса на создание заказа. Вынесено из
 * OrderService в рамках ЛР1 (SRP).
 */
@Component
public class OrderValidator {

    public void validate(CreateOrderRequest request) {
        validateCustomerFullName(request);
        validateCustomerAddress(request);
        validateItems(request);
    }

    public void validateQuantity(Integer quantity) {
        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }

    private void validateCustomerFullName(CreateOrderRequest request) {
        if (request.customerFullName() == null || request.customerFullName().isBlank()) {
            throw new IllegalArgumentException("customerFullName is required");
        }
    }

    private void validateCustomerAddress(CreateOrderRequest request) {
        if (request.customerAddress() == null || request.customerAddress().isBlank()) {
            throw new IllegalArgumentException("customerAddress is required");
        }
    }

    private void validateItems(CreateOrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one item");
        }
    }
}