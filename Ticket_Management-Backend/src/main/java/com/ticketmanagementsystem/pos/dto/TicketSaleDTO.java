package com.ticketmanagementsystem.pos.dto;

public class TicketSaleDTO {
    private Long id;
    private String ticketName;
    private Long customerId;
    private Long vendorId;

    // Constructors
    public TicketSaleDTO() {}

    public TicketSaleDTO(Long id, String ticketName, Long customerId, Long vendorId) {
        this.id = id;
        this.ticketName = ticketName;
        this.customerId = customerId;
        this.vendorId = vendorId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
}