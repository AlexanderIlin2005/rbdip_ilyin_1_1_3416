package com.rbdip.bookstore.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Нормализованная сущность клиента (ЛР2). Раньше контактные данные
 * дублировались прямо в orders. Теперь хранятся здесь, а orders
 * ссылается по customer_id.
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    protected Customer() {
        // for JPA
    }

    public Customer(String fullName, String address, String phone) {
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}