import React from 'react';

const ControlPanel = ({ onStart, onStop }) => {
  return (
    <div className="control-panel">
      <div className="button-group">
        <button id="startButton" onClick={onStart}>Start</button>
        <button id="stopButton" onClick={onStop}>Stop</button>
      </div>
    </div>
  );
};

export default ControlPanel;