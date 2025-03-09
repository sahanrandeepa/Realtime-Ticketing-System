import React from 'react';

const CustomerCard = ({ count, onAdd, onRemove }) => {
  return (
    <div className="customer-card">
      <h3>Customers</h3>
      <p>Current Count: {count}</p>
      <div className="button-group">
        <button onClick={onAdd}>Add Customer</button>
        <button onClick={onRemove}>Remove Customer</button>
      </div>
    </div>
  );
};

export default CustomerCard;