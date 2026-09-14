package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Простейшая реализация OrderNotifier: пишет уведомление в лог.
 * Заменяет System.out.printf из оригинального OrderService
 * (устраняет PMD SystemPrintln).
 */
@Component
public class ConsoleOrderNotifier implements OrderNotifier {

    private static final Logger log = LoggerFactory.getLogger(ConsoleOrderNotifier.class);

    @Override
    public void orderCreated(String customerName, Long orderId, BigDecimal total) {
        log.info("[email] Dear {}, your order #{} for {} has been placed.", customerName, orderId, total);
    }
}