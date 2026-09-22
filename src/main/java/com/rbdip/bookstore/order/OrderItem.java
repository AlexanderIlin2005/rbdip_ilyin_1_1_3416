package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Позиция заказа. product_name/product_price убраны (нормализация ЛР2),
 * вместо них - product_id FK на products. Геттеры getProductName()
 * и getProductPrice() оставлены как прокси к product.name/price,
 * чтобы не ломать публичный API.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    protected OrderItem() {
        // for JPA
    }

    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return order.getId();
    }

    public Product getProduct() {
        return product;
    }

    public String getProductName() {
        return product.getName();
    }

    public java.math.BigDecimal getProductPrice() {
        return product.getPrice();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}