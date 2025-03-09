import React from 'react';

const ConfigurationForm = ({ config, onConfigChange, error }) => {
  const handleChange = (e) => {
    const { name, value } = e.target;
    onConfigChange({ ...config, [name]: value });
  };

  return (
    <div className="configuration-form">
      <h2>Configuration</h2>
      {error && <div className="error-message">{error}</div>}
      <div className="input-group">
        <label>Total Tickets</label>
        <input
          type="number"
          name="totalTickets"
          value={config.totalTickets}
          onChange={handleChange}
          min="1"
        />
      </div>
      <div className="input-group">
        <label>Ticket Release Rate</label>
        <input
          type="number"
          name="ticketReleaseRate"
          value={config.ticketReleaseRate}
          onChange={handleChange}
          min="100"
        />
      </div>
      <div className="input-group">
        <label>Customer Retrieval Rate</label>
        <input
          type="number"
          name="customerRetrievalRate"
          value={config.customerRetrievalRate}
          onChange={handleChange}
          min="1"
        />
      </div>
      <div className="input-group">
        <label>Max Ticket Capacity</label>
        <input
          type="number"
          name="maxTicketCapacity"
          value={config.maxTicketCapacity}
          onChange={handleChange}
          min="1"
        />
      </div>
    </div>
  );
};

export default ConfigurationForm;