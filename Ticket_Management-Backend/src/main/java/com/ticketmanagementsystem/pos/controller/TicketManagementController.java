package com.ticketmanagementsystem.pos.controller;

import com.ticketmanagementsystem.pos.dto.ConfigDTO;
import com.ticketmanagementsystem.pos.dto.TicketStatusDTO;
import com.ticketmanagementsystem.pos.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/customer")
public class TicketManagementController {

    private final SimulationService simulationService;

    @Autowired
    public TicketManagementController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/configure")
    public String configureSimulation(@RequestBody ConfigDTO configDTO) {
        try {
            simulationService.configureSimulation(configDTO);
            return "Simulation configured successfully.";
        } catch (IllegalArgumentException e) {
            return "Configuration failed: " + e.getMessage();
        }
    }

    @PostMapping("/start")
    public String startSimulation() {
        try {
            simulationService.startSimulation();
            return "Simulation started.";
        } catch (IllegalStateException e) {
            return "Failed to start simulation: " + e.getMessage();
        }
    }

    @PostMapping("/stop")
    public String stopSimulation() {
        simulationService.stopSimulation();
        return "Simulation stopped.";
    }

    @GetMapping("/config")
    public ConfigDTO getConfiguration() {
        ConfigDTO config = simulationService.loadConfigFromFile();
        if (config == null) {
            throw new RuntimeException("No configuration file found.");
        }
        return config;
    }

    @GetMapping("/status")
    public TicketStatusDTO getTicketStatus() {
        return simulationService.getTicketStatus();
    }

    @PostMapping("/add-vendor")
    public String addVendor() {
        simulationService.addVendor();
        return "Vendor added.";
    }

    @PostMapping("/remove-vendor")
    public String removeVendor() {
        simulationService.removeVendor();
        return "Vendor removed.";
    }

    @PostMapping("/add-customer")
    public String addCustomer() {
        simulationService.addCustomer();
        return "Customer added.";
    }

    @PostMapping("/remove-customer")
    public String removeCustomer() {
        simulationService.removeCustomer();
        return "Customer removed.";
    }

    @GetMapping("/vendor-count")
    public int getVendorCount() {
        return simulationService.getVendorCount();
    }

    @GetMapping("/customer-count")
    public int getCustomerCount() {
        return simulationService.getCustomerCount();
    }

    @MessageMapping("/sendLog")
    @SendTo("/topic/logs")
    public String sendLog(String logMessage) {
        return logMessage;
    }
}