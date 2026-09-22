package com.rbdip.bookstore.order;

import com.rbdip.bookstore.review.PurchaseVerifier;
import org.springframework.stereotype.Component;

/**
 * Реализация порта {@link PurchaseVerifier} (см. пакет review).
 * Живёт в модуле order, потому что знает про order_items. Направление
 * зависимости order -> review разрешено архитектурным правилом
 * (запрещено только обратное, review -> order).
 */
@Component
public class JpaPurchaseVerifier implements PurchaseVerifier {

    private final OrderItemRepository orderItemRepository;

    public JpaPurchaseVerifier(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean hasAnyPurchaseForProduct(Long productId) {
        return orderItemRepository.existsByProductId(productId);
    }
}