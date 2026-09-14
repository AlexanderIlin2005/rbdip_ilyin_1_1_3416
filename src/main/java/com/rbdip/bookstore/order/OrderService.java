package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Оркестрация создания заказа. В ЛР1 God-метод разбит на отдельные
 * ответственности: валидацию (OrderValidator), сбор строк
 * (OrderAssembler), персистентность (OrderPersister) и уведомление
 * (OrderNotifier). Расчёт цены остаётся в PricingCalculator.
 */
@Service
public class OrderService {

    private final OrderValidator orderValidator;
    private final OrderAssembler orderAssembler;
    private final OrderPersister orderPersister;
    private final OrderNotifier orderNotifier;
    private final PricingCalculator pricingCalculator = new PricingCalculator();

    public OrderService(
            OrderValidator orderValidator,
            OrderAssembler orderAssembler,
            OrderPersister orderPersister,
            OrderNotifier orderNotifier) {
        this.orderValidator = orderValidator;
        this.orderAssembler = orderAssembler;
        this.orderPersister = orderPersister;
        this.orderNotifier = orderNotifier;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        orderValidator.validate(request);

        OrderAssembler.AssembledOrder assembled = orderAssembler.assemble(request);

        String customerType = request.customerType() == null ? "regular" : request.customerType();
        BigDecimal total = pricingCalculator.calculateOrderTotal(
                assembled.lineItems(), customerType, request.couponCode());

        Order order = orderPersister.persist(request, assembled.products(), assembled.lineItems());

        orderNotifier.orderCreated(request.customerFullName(), order.getId(), total);

        return order;
    }
}