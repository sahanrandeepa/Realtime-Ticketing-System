import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import ConfigurationForm from './components/ConfigurationForm';
import TicketDisplay from './components/TicketDisplay';
import ControlPanel from './components/ControlPanel';
import LogDisplay from './components/LogDisplay';
import VendorCard from './components/VendorCard';
import CustomerCard from './components/CustomerCard';
import SalesChart from './components/SalesChart'; // Import the new component
import './index.css';

const API_BASE_URL = 'http://localhost:8080/api/v1/customer';
const SOCKET_URL = 'ws://localhost:8080/ws';

const App = () => {
  const [config, setConfig] = useState({
    totalTickets: '',
    ticketReleaseRate: '',
    customerRetrievalRate: '',
    maxTicketCapacity: '',
  });
  const [ticketStatus, setTicketStatus] = useState({
    totalTickets: 0,
    ticketsReleased: 0,
    ticketsSold: 0,
    ticketsAvailable: 0,
  });
  const [vendorCount, setVendorCount] = useState(0);
  const [customerCount, setCustomerCount] = useState(0);
  const [logs, setLogs] = useState([]);
  const [error, setError] = useState(null);
  const clientRef = useRef(null); // Store the STOMP client

  useEffect(() => {
    // Fetch initial configuration
    fetch(`${API_BASE_URL}/config`)
      .then((response) => response.ok ? response.json() : Promise.reject(response.status))
      .then((data) => {
        setConfig({
          totalTickets: data.totalTickets || '',
          ticketReleaseRate: data.ticketReleaseRate || '',
          customerRetrievalRate: data.customerRetrievalRate || '',
          maxTicketCapacity: data.maxTicketCapacity || '',
        });
      })
      .catch((err) => console.error('Error fetching config:', err));

    // WebSocket setup with raw WebSocket
    clientRef.current = new Client({
      brokerURL: SOCKET_URL,
      debug: (str) => console.log('STOMP Debug:', str),
      onConnect: () => {
        console.log('Connected to WebSocket');
        clientRef.current.subscribe('/topic/logs', (message) => {
          const newLog = message.body;
          console.log('Received log:', newLog);
          setLogs((prevLogs) => [...prevLogs, newLog]);
        });
      },
      onDisconnect: () => console.log('Disconnected from WebSocket'),
      onStompError: (error) => console.error('WebSocket STOMP Error:', error),
    });

    clientRef.current.activate();

    // Poll ticket status, vendor count, and customer count every 2 seconds
    const interval = setInterval(() => {
      fetch(`${API_BASE_URL}/status`)
        .then((response) => response.ok ? response.json() : Promise.reject(response.status))
        .then((data) => setTicketStatus(data))
        .catch((err) => console.error('Error fetching status:', err));

      fetch(`${API_BASE_URL}/vendor-count`)
        .then((response) => response.ok ? response.json() : Promise.reject(response.status))
        .then((count) => setVendorCount(count))
        .catch((err) => console.error('Error fetching vendor count:', err));

      fetch(`${API_BASE_URL}/customer-count`)
        .then((response) => response.ok ? response.json() : Promise.reject(response.status))
        .then((count) => setCustomerCount(count))
        .catch((err) => console.error('Error fetching customer count:', err));
    }, 2000);

    return () => {
      clientRef.current.deactivate();
      clearInterval(interval);
    };
  }, []);

  const handleConfigChange = (newConfig) => {
    setConfig(newConfig);
  };

  const handleStart = () => {
    fetch(`${API_BASE_URL}/configure`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(config),
    })
      .then((response) => response.ok ? response.text() : Promise.reject(response.statusText))
      .then((message) => {
        setError(null);
        return fetch(`${API_BASE_URL}/start`, { method: 'POST' });
      })
      .then((response) => response.ok ? response.text() : Promise.reject(response.statusText))
      .then((message) => setError(null))
      .catch((err) => setError(`Error: ${err}`));
  };

  const handleStop = () => {
    fetch(`${API_BASE_URL}/stop`, { method: 'POST' })
      .then((response) => response.ok ? response.text() : Promise.reject(response.statusText))
      .then((message) => setError(null))
      .catch((err) => setError(`Error: ${err}`));
  };

  const handleAddVendor = () => {
    fetch(`${API_BASE_URL}/add-vendor`, { method: 'POST' })
      .then((response) => response.ok ? response.text() : Promise.reject(response.statusText))
      .catch((err) => console.error('Error adding vendor:', err));
  };

  const handleRemoveVendor = () => {
    fetch(`${API_BASE_URL}/remove-vendor`, { method: 'POST' })
      .then((response) => response.ok ? response.text() : Promise.reject(response.statusText))
      .catch((err) => console.error('Error removing vendor:', err));
  };

  const handleAddCustomer = () => {
    fetch(`${API_BASE_URL}/add-customer`, { method: 'POST' })
      .then((response) => response.ok ? response.text() : Promise.reject(response.statusText))
      .catch((err) => console.error('Error adding customer:', err));
  };

  const handleRemoveCustomer = () => {
    fetch(`${API_BASE_URL}/remove-customer`, { method: 'POST' })
      .then((response) => response.ok ? response.text() : Promise.reject(response.statusText))
      .catch((err) => console.error('Error removing customer:', err));
  };

  return (
    <div className="container">
      <div className="left-column">
        <ConfigurationForm config={config} onConfigChange={handleConfigChange} error={error} />
        <ControlPanel onStart={handleStart} onStop={handleStop} />
        <VendorCard count={vendorCount} onAdd={handleAddVendor} onRemove={handleRemoveVendor} />
        <CustomerCard count={customerCount} onAdd={handleAddCustomer} onRemove={handleRemoveCustomer} />
      </div>
      <div className="right-column">
        <TicketDisplay status={ticketStatus} />
        <SalesChart client={clientRef.current} /> {/* Add the SalesChart component */}
        <LogDisplay logs={logs} />
      </div>
    </div>
  );
};

export default App;