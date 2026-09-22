package com.rbdip.bookstore.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o "
            + "JOIN FETCH o.customer "
            + "LEFT JOIN FETCH o.items i "
            + "LEFT JOIN FETCH i.product")
    List<Order> findAllWithItemsAndCustomer();
}
