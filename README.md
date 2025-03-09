# Real-Time Event Ticketing System

This project implements a Real-Time Event Ticketing System using the **Producer-Consumer Pattern**. The system simulates concurrent ticket releases by vendors (producers) and purchases by customers (consumers) while ensuring data integrity in a multi-threaded environment. The project is built using **Spring Boot** for the backend, **React.js** for the frontend, and **MySQL** as the database.

This project was developed as part of my university coursework.

---

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Technology Stack](#technology-stack)
4. [System Design](#system-design)
5. [Configuration](#configuration)
6. [Logging and Error Handling](#logging-and-error-handling)
7. [Advanced Functionalities](#advanced-functionalities)

---

## Introduction
The Real-Time Event Ticketing System is designed to handle concurrent ticket releases and purchases in a dynamic environment. The system uses multi-threading and synchronization to ensure data integrity and provides a user-friendly interface for configuration and monitoring.

---

## Features
- **Concurrency Handling**: Supports multiple producers (vendors) and consumers (customers) operating concurrently.
- **Data Integrity**: Ensures thread-safe operations using synchronization mechanisms.
- **Dynamic Configuration**: Allows users to configure system parameters such as ticket release rate, customer retrieval rate, and maximum ticket capacity.
- **Real-Time Monitoring**: Provides a user interface to monitor ticket availability and system status.
- **Logging and Error Handling**: Implements logging for system activities and handles exceptions gracefully.
- **Database Integration**: Uses MySQL for persistent storage of ticket sales, customer data, and vendor data.

---

## Technology Stack
- **Frontend**: React.js
- **Backend**: Spring Boot
- **Database**: MySQL
- **Logging**: Log4j or java.util.logging (Java) / console.log (JavaScript).
- **Build Tools**: Maven (Spring Boot) / npm (React).

---

## System Design
The system is divided into the following components:
1. **Ticket Vendor (Producer)**:
   - Simulates multiple vendors releasing tickets concurrently.
   - Each vendor operates independently and adds tickets to a shared ticket pool.
2. **Customer (Consumer)**:
   - Simulates multiple customers purchasing tickets concurrently.
   - Each customer attempts to purchase tickets from the shared ticket pool.
3. **Ticket Pool**:
   - A shared resource managed using thread-safe data structures.
   - Ensures safe concurrent access for adding and removing tickets.
4. **User Interface**:
   - Built using React.js for real-time monitoring and configuration.
   - Displays current ticket availability, system logs, and allows starting/stopping the system.
5. **Database**:
   - MySQL is used to store ticket sales, customer details, and vendor information.
   - Implements CRUD operations for managing data.

---

## Configuration
The system allows users to configure the following parameters:
- **Total Number of Tickets**: Total tickets available for the event.
- **Ticket Release Rate**: Rate at which vendors release tickets.
- **Customer Retrieval Rate**: Rate at which customers purchase tickets.
- **Maximum Ticket Capacity**: Maximum number of tickets the system can handle.

### Configuration Interface
- **React.js Frontend**:
  - Provides input fields for configuration parameters.
  - Validates inputs and sends them to the backend via REST API.

---

## Logging and Error Handling
- **Logging**:
  - System activities are logged to the console and/or a file.
  - Use `java.util.logging` or Log4j for backend logging.
- **Error Handling**:
  - Exceptions are caught and logged with meaningful error messages.
  - The UI displays error messages to the user.

---

## Advanced Functionalities
1. **Priority Customers**:
   - Implement VIP customers with priority access to tickets.
2. **Dynamic Vendor/Customer Management**:
   - Add or remove vendors and customers at runtime.
3. **Real-Time Analytics Dashboard**:
   - Visualize ticket sales and system performance using charts.
4. **Persistence**:
   - Save ticket sales, customer data, and vendor data to MySQL.

---

**Developed as part of my university coursework.** 🎓
