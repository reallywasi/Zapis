import React, { useState } from "react";
import "./Login.css";

const Login = () => {
  const [formData, setFormData] = useState({
    university: "",
    role: "",
    username: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);

  const universities = [
    "Select University",
    "Harvard University",
    "Stanford University",
    "MIT",
    "University of Oxford",
    "California Institute of Technology",
  ];

  const roles = ["Select Role", "APO", "Student", "Faculty"];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Form Submitted", formData);
  };

  return (
    <div className="login-container">
      <h1>Login</h1>
      <form onSubmit={handleSubmit} className="login-form">
        {/* University Dropdown */}
        <div className="form-group">
          <label htmlFor="university">University:</label>
          <select
            name="university"
            id="university"
            value={formData.university}
            onChange={handleChange}
            required
            className="input-box"
          >
            {universities.map((uni, index) => (
              <option key={index} value={uni}>
                {uni}
              </option>
            ))}
          </select>
        </div>

        {/* Role Dropdown */}
        <div className="form-group">
          <label htmlFor="role">Role:</label>
          <select
            name="role"
            id="role"
            value={formData.role}
            onChange={handleChange}
            required
            className="input-box"
          >
            {roles.map((role, index) => (
              <option key={index} value={role}>
                {role}
              </option>
            ))}
          </select>
        </div>

        {/* Username Input */}
        <div className="form-group">
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            name="username"
            value={formData.username}
            onChange={handleChange}
            placeholder="Enter your username"
            required
            className="input-box"
          />
        </div>

        {/* Password Input with Toggle Icon */}
        <div className="form-group password-group">
          <label htmlFor="password">Password:</label>
          <div className="password-container">
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
              required
              className="input-box"
            />
            <span
              className={`toggle-password ${
                showPassword ? "visible" : "hidden"
              }`}
              onClick={togglePasswordVisibility}
            >
              {showPassword ? "👁️" : "🙈"}
            </span>
          </div>
        </div>

        {/* Submit Button */}
        <button type="submit" className="submit-btn">
          Login
        </button>
      </form>
    </div>
  );
};

export default Login;
