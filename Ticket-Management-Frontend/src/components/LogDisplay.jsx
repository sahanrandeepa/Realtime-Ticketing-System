import React, { useRef, useEffect } from 'react';

const LogDisplay = ({ logs }) => {
  const logBoxRef = useRef(null); // Create a ref for the log container

  // Scroll to the bottom whenever logs change
  useEffect(() => {
    if (logBoxRef.current) {
      logBoxRef.current.scrollTop = logBoxRef.current.scrollHeight; // Scroll to the bottom
    }
  }, [logs]); // Trigger on logs change

  return (
    <div className="log-display">
      <h2>Logs</h2>
      <div className="log-box" ref={logBoxRef}>
        {logs.map((log, index) => (
          <div key={index} className="log-entry">{log}</div>
        ))}
      </div>
    </div>
  );
};

export default LogDisplay;