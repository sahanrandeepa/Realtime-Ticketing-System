import React from 'react';

const VendorCard = ({ count, onAdd, onRemove }) => {
  return (
    <div className="vendor-card">
      <h3>Vendors</h3>
      <p>Current Count: {count}</p>
      <div className="button-group">
        <button onClick={onAdd}>Add Vendor</button>
        <button onClick={onRemove}>Remove Vendor</button>
      </div>
    </div>
  );
};

export default VendorCard;