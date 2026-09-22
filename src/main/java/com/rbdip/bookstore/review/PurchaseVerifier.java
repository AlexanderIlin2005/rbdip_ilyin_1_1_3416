package com.rbdip.bookstore.review;

/**
 * Порт: review спрашивает
 * "покупал ли кто-нибудь этот товар?", не зная, откуда берётся ответ.
 * Реализация живёт в модуле order в методе order.JpaPurchaseVerifier.

 * Интерфейс введён в ЛР4 по паттерну Strangler Fig / DIP. review
 * больше не зависит от внутренних репозиториев order, только от
 * собственного контракта. ArchUnit-правило запрещает направление
 * review -> order, но разрешает обратное, чем
 * пользуется реализация порта.
 */
public interface PurchaseVerifier {

    /**
     * @return true, если существует хотя бы одна позиция заказа
     *         с указанным product_id
     */
    boolean hasAnyPurchaseForProduct(Long productId);
}