package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Модуль расчёта цены заказа. Намеренно почти не покрыт тестами и
 * содержит magic numbers / нечитаемые ветвления скидок - цель для
 * характеризационных тестов (ЛР2) и mutation-testing гейта PIT (ЛР5).
 */
public class PricingCalculator {

    public record LineItem(BigDecimal price, int quantity) {
    }

    private static final int BULK_QUANTITY_THRESHOLD = 10;
    private static final BigDecimal BULK_LINE_DISCOUNT = new BigDecimal("0.95");
    private static final String CUSTOMER_TYPE_VIP = "vip";
    private static final BigDecimal VIP_DISCOUNT = new BigDecimal("0.90");
    private static final String CUSTOMER_TYPE_WHOLESALE = "wholesale";
    private static final BigDecimal WHOLESALE_DISCOUNT = new BigDecimal("0.85");
    private static final String COUPON_SAVE10 = "SAVE10";
    private static final BigDecimal COUPON_SAVE10_AMOUNT = BigDecimal.TEN;
    private static final String COUPON_SAVE20PERCENT = "SAVE20PERCENT";
    private static final BigDecimal COUPON_SAVE20PERCENT_MULTIPLIER = new BigDecimal("0.80");
    private static final BigDecimal LARGE_ORDER_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal LARGE_ORDER_DISCOUNT = new BigDecimal("0.98");
    private static final int MONEY_SCALE = 2;

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = BigDecimal.ZERO;

        for (LineItem item : items) {
            BigDecimal linePrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
            if (item.quantity() > BULK_QUANTITY_THRESHOLD) {
                linePrice = linePrice.multiply(BULK_LINE_DISCOUNT);
            }
            total = total.add(linePrice);
        }

        if (CUSTOMER_TYPE_VIP.equals(customerType)) {
            total = total.multiply(VIP_DISCOUNT);
        } else if (CUSTOMER_TYPE_WHOLESALE.equals(customerType)) {
            total = total.multiply(WHOLESALE_DISCOUNT);
        }

        if (COUPON_SAVE10.equals(couponCode)) {
            total = total.subtract(COUPON_SAVE10_AMOUNT);
        } else if (COUPON_SAVE20PERCENT.equals(couponCode)) {
            total = total.multiply(COUPON_SAVE20PERCENT_MULTIPLIER);
        }

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        if (total.compareTo(LARGE_ORDER_THRESHOLD) > 0) {
            total = total.multiply(LARGE_ORDER_DISCOUNT);
        }

        return total.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}