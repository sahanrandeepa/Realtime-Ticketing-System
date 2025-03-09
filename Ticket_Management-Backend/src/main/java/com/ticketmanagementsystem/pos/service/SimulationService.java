package com.ticketmanagementsystem.pos.service;

import com.ticketmanagementsystem.pos.dto.*;

import java.util.List;


public interface SimulationService {
    void configureSimulation(ConfigDTO configDTO);
    void startSimulation();
    void stopSimulation();
    ConfigDTO loadConfigFromFile();
    TicketStatusDTO getTicketStatus();
    void addVendor(); // New method to add a vendor
    void removeVendor(); // New method to remove a vendor
    void addCustomer(); // New method to add a customer
    void removeCustomer(); // New method to remove a customer
    int getVendorCount(); // Get current number of vendors
    int getCustomerCount();

    void saveTicketSale(String ticketName, Long customerId, Long vendorId); // Add this method
    List<TicketSaleDTO> getAllTicketSales();
    TicketSaleDTO getTicketSaleById(Long id);
    void deleteTicketSale(Long id);

    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(Long id);
    void deleteCustomer(Long id);

    List<VendorDTO> getAllVendors();
    VendorDTO getVendorById(Long id);
    void deleteVendor(Long id);
}
