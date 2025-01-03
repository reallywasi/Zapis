import React from "react";
import "./App.css";
import Login from "./Login";

function App() {
  return (
    <div className="app">
      {/* Header */}
      <header className="header">
        <div className="logo">Zapis</div>
        <nav className="nav">
          <a href="#about">About</a>
          <a href="#features">Features</a>
          <a href="#register">Register</a>
        </nav>
      </header>

      <Login />
      {/* Footer */}
      <footer className="footer">
        <p>&copy; {new Date().getFullYear()} Zapis. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default App;
