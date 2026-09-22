package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Сохраняет заказ и его позиции в БД. Customer ищется по
 * (fullName, address, phone), если не найден - создаётся.
 */
@Component
public class OrderPersister {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    public OrderPersister(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
    }

    public Order persist(CreateOrderRequest request, List<Product> products, List<PricingCalculator.LineItem> lineItems) {
        Customer customer = findOrCreateCustomer(request);
        Order order = new Order(customer, "new");
        order = orderRepository.save(order);

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = lineItems.get(i).quantity();
            orderItemRepository.save(new OrderItem(order.getId(), product, quantity));
        }

        return order;
    }

    private Customer findOrCreateCustomer(CreateOrderRequest request) {
        List<Customer> existing = customerRepository.findByFullNameAndAddressAndPhone(
                request.customerFullName(), request.customerAddress(), request.customerPhone());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        return customerRepository.save(new Customer(
                request.customerFullName(), request.customerAddress(), request.customerPhone()));
    }
}