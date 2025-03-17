"use client"
import Image from "next/image";

export default function Home() {
  return (
    <div>
      <div className="navbar bg-base-100 px-6 shadow-md">
  {/* Left Section (Logo) */}
  <div className="navbar-start">
    <a className="text-2xl font-bold text-primary flex items-center gap-2">
      <img src="/logo.png" alt="Zapis Logo" className="h-8 w-8" /> Zapis
    </a>
  </div>

  {/* Center Section (Navigation) */}
  <div className="navbar-center hidden lg:flex">
    <ul className="menu menu-horizontal px-2 space-x-4">
      <li><a className="hover:text-primary">Dashboard</a></li>
      <li><a className="hover:text-primary">Features</a></li>
      <li><a className="hover:text-primary">Pricing</a></li>
      <li><a className="hover:text-primary">Contact</a></li>
    </ul>
  </div>

  {/* Right Section (Login Dropdown & Mobile Menu) */}
  <div className="navbar-end flex gap-4">
    {/* Login Dropdown */}
    <div className="dropdown dropdown-end">
      <div tabIndex={0} role="button" className="btn btn-outline btn-primary">
        Login
      </div>
      <ul 
        tabIndex={0} 
        className="menu dropdown-content bg-base-100 rounded-box mt-3 w-48 shadow-lg z-[10]">
        <li><a className="hover:text-primary">Admin Login</a></li>
        <li><a className="hover:text-primary">Student Login</a></li>
        <li><a className="hover:text-primary">Faculty Login</a></li>
      </ul>
    </div>

    {/* Mobile Menu */}
    <div className="dropdown lg:hidden">
      <div tabIndex={0} role="button" className="btn btn-ghost">
        <svg 
          xmlns="http://www.w3.org/2000/svg" 
          className="h-6 w-6" 
          fill="none" 
          viewBox="0 0 24 24" 
          stroke="currentColor">
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth="2" 
            d="M4 6h16M4 12h8m-8 6h16" />
        </svg>
      </div>
      <ul 
        tabIndex={0} 
        className="menu menu-sm dropdown-content bg-base-100 rounded-box mt-3 w-48 p-2 shadow-lg">
        <li><a className="hover:text-primary">Dashboard</a></li>
        <li><a className="hover:text-primary">Features</a></li>
        <li><a className="hover:text-primary">Pricing</a></li>
        <li><a className="hover:text-primary">Contact</a></li>
        <li className="menu-title">Login</li>
        <li><a className="hover:text-primary">Admin</a></li>
        <li><a className="hover:text-primary">Student</a></li>
        <li><a className="hover:text-primary">Faculty</a></li>
      </ul>
    </div>
  </div>
</div>



  </div>
  );
}
