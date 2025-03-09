import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';

// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

const SalesChart = ({ client }) => {
  const [ticketSalesData, setTicketSalesData] = useState({
    labels: [],
    datasets: [
      {
        label: 'Ticket Sales Over Time',
        data: [],
        borderColor: 'rgba(75, 192, 192, 1)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        fill: true,
      },
    ],
  });

  // Fetch initial ticket sales data
  useEffect(() => {
    fetch('http://localhost:8080/api/v1/customer/ticket-sales')
      .then((response) => response.ok ? response.json() : Promise.reject(response.status))
      .then((data) => {
        const labels = data.map(() => new Date().toLocaleTimeString());
        const salesData = data.map((_, index) => index + 1); // Incremental count for initial data
        setTicketSalesData({
          labels,
          datasets: [
            {
              label: 'Ticket Sales Over Time',
              data: salesData,
              borderColor: 'rgba(75, 192, 192, 1)',
              backgroundColor: 'rgba(75, 192, 192, 0.2)',
              fill: true,
            },
          ],
        });
      })
      .catch((err) => console.error('Error fetching initial ticket sales:', err));
  }, []);

  // Subscribe to real-time ticket sales updates
  useEffect(() => {
    if (!client || !client.connected) return;

    const subscription = client.subscribe('/topic/ticket-sales', (message) => {
      try {
        const ticketSale = JSON.parse(message.body);
        console.log('New ticket sale received:', ticketSale);
        setTicketSalesData((prevData) => {
          const newLabels = [...prevData.labels, new Date().toLocaleTimeString()].slice(-20);
          const newData = [...prevData.datasets[0].data, prevData.datasets[0].data.length + 1].slice(-20);
          return {
            ...prevData,
            labels: newLabels,
            datasets: [
              {
                ...prevData.datasets[0],
                data: newData,
              },
            ],
          };
        });
      } catch (e) {
        console.error('Error parsing ticket sale message:', e);
      }
    });

    return () => subscription.unsubscribe();
  }, [client]);

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: { position: 'top' },
      title: { display: true, text: 'Ticket Sales Over Time' },
    },
    scales: {
      x: { title: { display: true, text: 'Time' } },
      y: { title: { display: true, text: 'Number of Tickets Sold' }, beginAtZero: true },
    },
  };

  return (
    <div className="sales-chart">
      <h2>Ticket Sales Chart</h2>
      <Line data={ticketSalesData} options={chartOptions} />
    </div>
  );
};

export default SalesChart;