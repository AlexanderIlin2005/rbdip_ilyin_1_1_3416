package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.order.PricingCalculator.LineItem;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Характеризационные тесты PricingCalculator (ЛР2). Фиксируют текущее
 * поведение всех ветвей расчёта: bulk-скидка, тип клиента, купоны,
 * floor, скидка за крупный заказ и округление. Используются как опора
 * для mutation-testing гейта PIT (ЛР5).
 */
class PricingCalculatorCharacterizationTest {

    private final PricingCalculator calculator = new PricingCalculator();

    // --- База ---

    @Test
    @DisplayName("regular без купона: цена x количество")
    void regularWithoutCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 2)), "regular", null);

        assertThat(total).isEqualByComparingTo("20.00");
    }

    // --- Bulk-скидка ---

    @Test
    @DisplayName("qty=10 не даёт bulk-скидку (граница, условие строгое)")
    void bulkDiscountNotAppliedAtThreshold() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 10)), "regular", null);

        assertThat(total).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("qty=11 даёт bulk-скидку 0.95")
    void bulkDiscountAppliedAboveThreshold() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 11)), "regular", null);

        assertThat(total).isEqualByComparingTo("104.50"); // 110 * 0.95
    }

    // --- Customer type ---

    @Test
    @DisplayName("vip: скидка 10%")
    void vipDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "vip", null);

        assertThat(total).isEqualByComparingTo("90.00");
    }

    @Test
    @DisplayName("wholesale: скидка 15%")
    void wholesaleDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "wholesale", null);

        assertThat(total).isEqualByComparingTo("85.00");
    }

    @Test
    @DisplayName("неизвестный тип клиента: без скидки")
    void unknownCustomerType() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "gold", null);

        assertThat(total).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("null-тип клиента: без скидки")
    void nullCustomerType() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), null, null);

        assertThat(total).isEqualByComparingTo("100.00");
    }

    // --- Coupons ---

    @Test
    @DisplayName("SAVE10: абсолютная скидка 10")
    void couponSave10() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("50.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("40.00");
    }

    @Test
    @DisplayName("SAVE20PERCENT: скидка 20%")
    void couponSave20Percent() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("50.00"), 1)), "regular", "SAVE20PERCENT");

        assertThat(total).isEqualByComparingTo("40.00");
    }

    @Test
    @DisplayName("неизвестный купон: без изменений")
    void unknownCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("50.00"), 1)), "regular", "NOPE");

        assertThat(total).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("null-купон: без изменений")
    void nullCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("50.00"), 1)), "regular", null);

        assertThat(total).isEqualByComparingTo("50.00");
    }

    // --- Floor ---

    @Test
    @DisplayName("отрицательный итог после SAVE10 обнуляется")
    void negativeTotalFlooredToZero() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("5.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("ровно 0 после скидки остаётся 0")
    void exactlyZeroTotal() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("0.00");
    }

    // --- Large order ---

    @Test
    @DisplayName("total=1000: скидка за крупный заказ НЕ применяется (граница)")
    void largeOrderAtThreshold() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("1000.00"), 1)), "regular", null);

        assertThat(total).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("total=1000.01: скидка за крупный заказ применяется")
    void largeOrderAboveThreshold() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("1000.01"), 1)), "regular", null);

        // 1000.01 * 0.98 = 980.0098 -> scale 2 HALF_UP -> 980.01
        assertThat(total).isEqualByComparingTo("980.01");
    }

    // --- Комбинации ---

    @Test
    @DisplayName("vip + SAVE10: применяется по порядку")
    void vipPlusSave10() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "vip", "SAVE10");

        // 100 * 0.9 = 90; 90 - 10 = 80
        assertThat(total).isEqualByComparingTo("80.00");
    }

    @Test
    @DisplayName("wholesale + qty=11: bulk и wholesale")
    void wholesalePlusBulk() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 11)), "wholesale", null);

        // 10*11 = 110; bulk -> 110*0.95 = 104.5; wholesale -> 104.5*0.85 = 88.825 -> 88.83
        assertThat(total).isEqualByComparingTo("88.83");
    }

    @Test
    @DisplayName("vip + SAVE20PERCENT + total>1000: три правила подряд")
    void vipSave20AndLargeOrder() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("2000.00"), 1)), "vip", "SAVE20PERCENT");

        // 2000 * 0.9 = 1800; 1800 * 0.8 = 1440; 1440 > 1000 -> 1440 * 0.98 = 1411.2 -> 1411.20
        assertThat(total).isEqualByComparingTo("1411.20");
    }

    // --- Округление ---

    @Test
    @DisplayName("округление HALF_UP: 10.005 -> 10.01")
    void roundingHalfUp() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.005"), 1)), "regular", null);

        assertThat(total).isEqualByComparingTo("10.01");
    }

    @Test
    @DisplayName("округление HALF_UP: 0.004 -> 0.00")
    void roundingDown() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("0.004"), 1)), "regular", null);

        assertThat(total).isEqualByComparingTo("0.00");
    }

    // --- Прочее ---

    @Test
    @DisplayName("пустой список: 0.00")
    void emptyItems() {
        BigDecimal total = calculator.calculateOrderTotal(List.of(), "regular", null);

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("несколько строк суммируются")
    void multipleItems() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(
                        new LineItem(new BigDecimal("10.00"), 2),
                        new LineItem(new BigDecimal("5.00"), 3)),
                "regular",
                null);

        assertThat(total).isEqualByComparingTo("35.00");
    }
}