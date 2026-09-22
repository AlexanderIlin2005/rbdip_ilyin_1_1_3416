package com.rbdip.bookstore.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByFullNameAndAddressAndPhone(String fullName, String address, String phone);
}