package com.ticketmanagementsystem.pos.repo;

import com.ticketmanagementsystem.pos.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByCustomerId(int customerId); // Custom query method
}