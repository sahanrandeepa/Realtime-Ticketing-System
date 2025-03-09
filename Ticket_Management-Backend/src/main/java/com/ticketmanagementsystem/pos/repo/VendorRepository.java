package com.ticketmanagementsystem.pos.repo;

import com.ticketmanagementsystem.pos.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Vendor findByVendorId(int vendorId); // Custom query method
}