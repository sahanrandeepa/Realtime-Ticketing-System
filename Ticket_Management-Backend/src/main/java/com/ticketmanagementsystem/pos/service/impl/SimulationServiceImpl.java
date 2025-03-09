package com.ticketmanagementsystem.pos.service.impl;

import com.ticketmanagementsystem.pos.dto.ConfigDTO;
import com.ticketmanagementsystem.pos.dto.TicketStatusDTO;
import com.ticketmanagementsystem.pos.dto.TicketSaleDTO;
import com.ticketmanagementsystem.pos.dto.CustomerDTO;
import com.ticketmanagementsystem.pos.dto.VendorDTO;
import com.ticketmanagementsystem.pos.entity.TicketSale;
import com.ticketmanagementsystem.pos.entity.Customer;
import com.ticketmanagementsystem.pos.entity.Vendor;
import com.ticketmanagementsystem.pos.repo.TicketSaleRepository;
import com.ticketmanagementsystem.pos.repo.CustomerRepository;
import com.ticketmanagementsystem.pos.repo.VendorRepository;
import com.ticketmanagementsystem.pos.service.SimulationService;
import com.ticketmanagementsystem.pos.util.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SimulationServiceImpl implements SimulationService {
    private static final Logger logger = LoggerFactory.getLogger(SimulationServiceImpl.class);

    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;
    private boolean isSimulationRunning = false;

    private final PriorityQueue<String> ticketPool = new PriorityQueue<>(new TicketPriorityComparator());
    private ExecutorService vendorExecutor;
    private ExecutorService customerExecutor;
    private final AtomicInteger ticketsReleased = new AtomicInteger(0);
    private final AtomicInteger ticketsSold = new AtomicInteger(0);

    private final List<Runnable> vendorTasks = new ArrayList<>();
    private final List<Runnable> customerTasks = new ArrayList<>();
    private final AtomicInteger vendorCount = new AtomicInteger(0);
    private final AtomicInteger customerCount = new AtomicInteger(0);

    @Autowired
    private FileHandler fileHandler;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TicketSaleRepository ticketSaleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    public SimulationServiceImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        logger.info("SimulationServiceImpl initialized. Repositories: ticketSaleRepository={}, customerRepository={}, vendorRepository={}",
                ticketSaleRepository != null, customerRepository != null, vendorRepository != null);
    }

    @Override
    public void configureSimulation(ConfigDTO configDTO) {
        if (configDTO == null) {
            throw new IllegalArgumentException("Configuration data is missing.");
        }

        this.totalTickets = configDTO.getTotalTickets();
        this.ticketReleaseRate = configDTO.getTicketReleaseRate();
        this.customerRetrievalRate = configDTO.getCustomerRetrievalRate();
        this.maxTicketCapacity = configDTO.getMaxTicketCapacity();

        try {
            fileHandler.saveConfigToFile(configDTO, "simulation-config.json");
            String logMessage = "Configuration saved: Total Tickets=" + totalTickets + ", Max Capacity=" + maxTicketCapacity;
            System.out.println("Sending log to /topic/logs: " + logMessage);
            messagingTemplate.convertAndSend("/topic/logs", logMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ConfigDTO loadConfigFromFile() {
        ConfigDTO config;
        try {
            config = fileHandler.readConfigFromFile("simulation-config.json");
            if (config != null) {
                String logMessage = "Configuration loaded from file.";
                System.out.println("Sending log to /topic/logs: " + logMessage);
                messagingTemplate.convertAndSend("/topic/logs", logMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (config == null) {
            throw new IllegalStateException("No configuration file found or the file is empty.");
        }
        return config;
    }

    @Override
    public void startSimulation() {
        if (isSimulationRunning) {
            throw new IllegalStateException("Simulation is already running.");
        }

        if (totalTickets == 0 || ticketReleaseRate == 0 || customerRetrievalRate == 0 || maxTicketCapacity == 0) {
            try {
                ConfigDTO configDTO = loadConfigFromFile();
                configureSimulation(configDTO);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to start simulation: Simulation is not properly configured.", e);
            }
        }

        isSimulationRunning = true;
        ticketsReleased.set(0);
        ticketsSold.set(0);
        ticketPool.clear();
        vendorTasks.clear();
        customerTasks.clear();
        vendorCount.set(0);
        customerCount.set(0);

        vendorExecutor = Executors.newFixedThreadPool(10);
        customerExecutor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 3; i++) {
            addVendor();
        }
        for (int i = 0; i < 5; i++) {
            addCustomer();
        }

        String logMessage = "Simulation started with " + totalTickets + " total tickets.";
        System.out.println("Sending log to /topic/logs: " + logMessage);
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }

    @Override
    public void stopSimulation() {
        isSimulationRunning = false;

        if (vendorExecutor != null) {
            vendorExecutor.shutdownNow();
        }
        if (customerExecutor != null) {
            customerExecutor.shutdownNow();
        }

        String stopMessage = "Simulation stopped. Tickets Released: " + ticketsReleased.get() +
                ", Tickets Sold: " + ticketsSold.get() + ", Pool Size: " + ticketPool.size();
        System.out.println(stopMessage);
        System.out.println("Sending log to /topic/logs: " + stopMessage);
        messagingTemplate.convertAndSend("/topic/logs", stopMessage);
    }

    @Override
    public TicketStatusDTO getTicketStatus() {
        synchronized (ticketPool) {
            return new TicketStatusDTO(totalTickets, ticketsReleased.get(), ticketsSold.get(), ticketPool.size());
        }
    }

    @Override
    public void addVendor() {
        if (!isSimulationRunning) {
            return;
        }
        int vendorId = vendorCount.incrementAndGet();
        Vendor vendor = vendorRepository.findByVendorId(vendorId);
        if (vendor == null) {
            vendor = new Vendor(vendorId);
            vendorRepository.save(vendor);
            logger.info("Saved new vendor with vendorId: {}", vendorId);
        }
        Long vendorDbId = vendor.getId();

        Runnable vendorTask = () -> {
            while (isSimulationRunning) {
                synchronized (ticketPool) {
                    if (ticketsReleased.get() >= totalTickets) {
                        break;
                    }

                    while (ticketPool.size() >= maxTicketCapacity) {
                        try {
                            ticketPool.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    int currentTicketNumber = ticketsReleased.incrementAndGet();
                    if (currentTicketNumber > totalTickets) {
                        break;
                    }

                    int priority = (currentTicketNumber % 5 == 0) ? 1 : 0;
                    String ticketName = "Vendor_" + vendorId + "_Ticket_" + currentTicketNumber + "_Priority_" + priority;
                    ticketPool.offer(ticketName);
                    String logMessage = ticketName + " added to the pool. Total tickets in pool: " + ticketPool.size();
                    System.out.println("Sending log to /topic/logs: " + logMessage);
                    messagingTemplate.convertAndSend("/topic/logs", logMessage);
                    ticketPool.notifyAll();
                }

                try {
                    Thread.sleep(ticketReleaseRate);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        };
        vendorTasks.add(vendorTask);
        vendorExecutor.submit(vendorTask);
        String logMessage = "Added Vendor_" + vendorId + ". Total vendors: " + vendorCount.get();
        System.out.println("Sending log to /topic/logs: " + logMessage);
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }

    @Override
    public void removeVendor() {
        if (!isSimulationRunning || vendorCount.get() <= 0) {
            return;
        }
        vendorCount.decrementAndGet();
        String logMessage = "Removed a vendor. Total vendors: " + vendorCount.get();
        System.out.println("Sending log to /topic/logs: " + logMessage);
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }

    @Override
    public void addCustomer() {
        if (!isSimulationRunning) {
            return;
        }
        int customerId = customerCount.incrementAndGet();
        int priority = (customerId <= 2) ? 1 : 0;
        Customer customer = customerRepository.findByCustomerId(customerId);
        if (customer == null) {
            customer = new Customer(customerId, priority);
            customerRepository.save(customer);
            logger.info("Saved new customer with customerId: {}", customerId);
        }
        Long customerDbId = customer.getId();

        Runnable customerTask = () -> {
            while (isSimulationRunning) {
                List<String> retrievedTickets = new ArrayList<>();

                synchronized (ticketPool) {
                    while (ticketPool.isEmpty()) {
                        try {
                            ticketPool.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    while (!ticketPool.isEmpty() && retrievedTickets.size() < customerRetrievalRate) {
                        String ticket = ticketPool.poll();
                        retrievedTickets.add(ticket);
                        ticketsSold.incrementAndGet();

                        // Extract vendorId from ticket name (e.g., "Vendor_1_Ticket_1_Priority_0")
                        String[] parts = ticket.split("_");
                        int vendorId = Integer.parseInt(parts[1]);
                        Vendor vendor = vendorRepository.findByVendorId(vendorId);
                        if (vendor != null) {
                            saveTicketSale(ticket, customerDbId, vendor.getId());
                            logger.info("Saved ticket sale: {} for customerId: {}, vendorId: {}", ticket, customerDbId, vendor.getId());
                        }
                    }

                    String retrieveMessage = "Customer_" + customerId + "_Priority_" + priority + " retrieved tickets: " + retrievedTickets;
                    String poolMessage = "Remaining tickets in pool: " + ticketPool.size();
                    System.out.println(retrieveMessage);
                    System.out.println("Sending log to /topic/logs: " + retrieveMessage);
                    messagingTemplate.convertAndSend("/topic/logs", retrieveMessage);
                    System.out.println("Sending log to /topic/logs: " + poolMessage);
                    messagingTemplate.convertAndSend("/topic/logs", poolMessage);
                    ticketPool.notifyAll();
                }

                if (ticketsSold.get() >= totalTickets) {
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (ticketsSold.get() >= totalTickets) {
                stopSimulation();
            }
        };
        customerTasks.add(customerTask);
        customerExecutor.submit(customerTask);
        String logMessage = "Added Customer_" + customerId + "_Priority_" + priority + ". Total customers: " + customerCount.get();
        System.out.println("Sending log to /topic/logs: " + logMessage);
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }

    @Override
    public void removeCustomer() {
        if (!isSimulationRunning || customerCount.get() <= 0) {
            return;
        }
        customerCount.decrementAndGet();
        String logMessage = "Removed a customer. Total customers: " + customerCount.get();
        System.out.println("Sending log to /topic/logs: " + logMessage);
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }

    @Override
    public int getVendorCount() {
        return vendorCount.get();
    }

    @Override
    public int getCustomerCount() {
        return customerCount.get();
    }

    @Override
    public void saveTicketSale(String ticketName, Long customerId, Long vendorId) {
        try {
            TicketSale ticketSale = new TicketSale(ticketName, customerId, vendorId);
            ticketSaleRepository.save(ticketSale);
            logger.info("Successfully saved ticket sale: {} for customerId: {}, vendorId: {}", ticketName, customerId, vendorId);

            TicketSaleDTO ticketSaleDTO = new TicketSaleDTO(ticketSale.getId(), ticketName, customerId, vendorId);
            String logMessage = "Saved ticket sale: " + ticketName + " to Customer ID " + customerId + " from Vendor ID " + vendorId;
            System.out.println("Sending log to /topic/logs: " + logMessage);
            messagingTemplate.convertAndSend("/topic/logs", logMessage);
            System.out.println("Sending ticket sale update to /topic/ticket-sales: " + ticketSaleDTO);
            messagingTemplate.convertAndSend("/topic/ticket-sales", ticketSaleDTO);
            logger.info("Broadcasted ticket sale to /topic/ticket-sales");
        } catch (Exception e) {
            logger.error("Failed to save ticket sale: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save ticket sale", e);
        }
    }

    @Override
    public List<TicketSaleDTO> getAllTicketSales() {
        return ticketSaleRepository.findAll().stream()
                .map(sale -> new TicketSaleDTO(sale.getId(), sale.getTicketName(), sale.getCustomerId(), sale.getVendorId()))
                .collect(Collectors.toList());
    }

    @Override
    public TicketSaleDTO getTicketSaleById(Long id) {
        TicketSale sale = ticketSaleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket Sale not found with ID: " + id));
        return new TicketSaleDTO(sale.getId(), sale.getTicketName(), sale.getCustomerId(), sale.getVendorId());
    }

    @Override
    public void deleteTicketSale(Long id) {
        ticketSaleRepository.deleteById(id);
        String logMessage = "Deleted ticket sale with ID: " + id;
        System.out.println("Sending log to /topic/logs: " + logMessage);
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> new CustomerDTO(customer.getId(), customer.getCustomerId(), customer.getPriority()))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        return new CustomerDTO(customer.getId(), customer.getCustomerId(), customer.getPriority());
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        String logMessage = "Deleted customer with ID: " + id;
        System.out.println("Sending log to /topic/logs: " + logMessage);
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }

    @Override
    public List<VendorDTO> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(vendor -> new VendorDTO(vendor.getId(), vendor.getVendorId()))
                .collect(Collectors.toList());
    }

    @Override
    public VendorDTO getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found with ID: " + id));
        return new VendorDTO(vendor.getId(), vendor.getVendorId());
    }

    @Override
    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
        String logMessage = "Deleted vendor with ID: " + id;
        System.out.println("Sending log to /topic/logs: " + logMessage);
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }

    private static class TicketPriorityComparator implements Comparator<String> {
        @Override
        public int compare(String ticket1, String ticket2) {
            int priority1 = extractPriority(ticket1);
            int priority2 = extractPriority(ticket2);
            return Integer.compare(priority2, priority1);
        }

        private int extractPriority(String ticket) {
            String[] parts = ticket.split("_Priority_");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1]);
            }
            return 0;
        }
    }
}