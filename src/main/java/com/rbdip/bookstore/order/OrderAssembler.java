package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Собирает строки заказа из запроса: загружает продукты, проверяет
 * количество, формирует LineItem для PricingCalculator. Вынесено из
 * OrderService в рамках ЛР1 (SRP).
 */
@Component
public class OrderAssembler {

    private final ProductRepository productRepository;
    private final OrderValidator orderValidator;

    public OrderAssembler(ProductRepository productRepository, OrderValidator orderValidator) {
        this.productRepository = productRepository;
        this.orderValidator = orderValidator;
    }

    public AssembledOrder assemble(CreateOrderRequest request) {
        List<Product> products = new ArrayList<>();
        List<PricingCalculator.LineItem> lineItems = new ArrayList<>();

        for (CreateOrderRequest.Item raw : request.items()) {
            Product product = productRepository.findById(raw.productId())
                    .orElseThrow(() -> new IllegalArgumentException("product " + raw.productId() + " not found"));
            orderValidator.validateQuantity(raw.quantity());
            int quantity = raw.quantity() == null ? 1 : raw.quantity();
            products.add(product);
            lineItems.add(new PricingCalculator.LineItem(product.getPrice(), quantity));
        }

        return new AssembledOrder(products, lineItems);
    }

    public record AssembledOrder(List<Product> products, List<PricingCalculator.LineItem> lineItems) {
    }
}