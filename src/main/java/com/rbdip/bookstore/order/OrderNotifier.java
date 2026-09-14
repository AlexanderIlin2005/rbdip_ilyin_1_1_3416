package com.rbdip.bookstore.order;

import java.math.BigDecimal;

/**
 * Уведомление клиента о созданном заказе. Интерфейс введён в ЛР1,
 * чтобы OrderService не знал о конкретном транспорте (сейчас - консоль).
 */
public interface OrderNotifier {
    void orderCreated(String customerName, Long orderId, BigDecimal total);
}