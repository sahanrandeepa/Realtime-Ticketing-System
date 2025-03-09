import React from 'react';

const TicketDisplay = ({ status }) => {
  return (
    <div className="ticket-display">
      <h2>Ticket Status</h2>
      <p>Total Tickets: {status.totalTickets}</p>
      <p>Tickets Released: {status.ticketsReleased}</p>
      <p>Tickets Sold: {status.ticketsSold}</p>
      <p>Tickets Available: {status.ticketsAvailable}</p>
    </div>
  );
};

export default TicketDisplay;