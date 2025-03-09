package com.ticketmanagementsystem.pos.dto;

public class TicketStatusDTO {
    private int totalTickets;
    private int ticketsReleased;
    private int ticketsSold;
    private int ticketsAvailable;

    public TicketStatusDTO(int totalTickets, int ticketsReleased, int ticketsSold, int ticketsAvailable) {
        this.totalTickets = totalTickets;
        this.ticketsReleased = ticketsReleased;
        this.ticketsSold = ticketsSold;
        this.ticketsAvailable = ticketsAvailable;
    }

    // Getters and Setters
    public int getTotalTickets() { return totalTickets; }
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }
    public int getTicketsReleased() { return ticketsReleased; }
    public void setTicketsReleased(int ticketsReleased) { this.ticketsReleased = ticketsReleased; }
    public int getTicketsSold() { return ticketsSold; }
    public void setTicketsSold(int ticketsSold) { this.ticketsSold = ticketsSold; }
    public int getTicketsAvailable() { return ticketsAvailable; }
    public void setTicketsAvailable(int ticketsAvailable) { this.ticketsAvailable = ticketsAvailable; }
}