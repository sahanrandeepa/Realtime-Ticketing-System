package com.ticketmanagementsystem.pos.repo;

import com.ticketmanagementsystem.pos.entity.TicketSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketSaleRepository extends JpaRepository<TicketSale, Long> {
}