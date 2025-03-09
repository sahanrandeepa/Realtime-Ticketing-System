package com.ticketmanagementsystem.pos.dto;

public class CustomerDTO {
    private Long id;
    private int customerId;
    private int priority;

    // Constructors
    public CustomerDTO() {}

    public CustomerDTO(Long id, int customerId, int priority) {
        this.id = id;
        this.customerId = customerId;
        this.priority = priority;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}