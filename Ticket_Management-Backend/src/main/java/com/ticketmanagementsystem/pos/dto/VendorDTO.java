package com.ticketmanagementsystem.pos.dto;

public class VendorDTO {
    private Long id;
    private int vendorId;

    // Constructors
    public VendorDTO() {}

    public VendorDTO(Long id, int vendorId) {
        this.id = id;
        this.vendorId = vendorId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }
}