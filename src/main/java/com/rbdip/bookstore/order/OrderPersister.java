package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Сохраняет заказ и его позиции в БД. Вынесено из OrderService в
 * рамках ЛР1 (SRP).
 */
@Component
public class OrderPersister {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPersister(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order persist(CreateOrderRequest request, List<Product> products, List<PricingCalculator.LineItem> lineItems) {
        Order order = new Order(
                request.customerFullName(), request.customerAddress(), request.customerPhone(), "new");
        order = orderRepository.save(order);

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = lineItems.get(i).quantity();
            orderItemRepository.save(new OrderItem(order.getId(), product.getName(), product.getPrice(), quantity));
        }

        return order;
    }
}