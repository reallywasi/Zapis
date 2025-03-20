"use client"
import Image from "next/image";
import AnimatedTestimonialsDemo from "@/components/testimonial";
import FeatureSection from "../components/FeaturesSection";
export default function Home() {
  return (
    <div className="">
   
   <section id="hero" className="min-h-screen bg-gray-900 text-white pt-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col lg:flex-row items-center justify-center h-[calc(100vh-4rem)]">
        <div className="lg:w-1/2 space-y-8 animate__animated animate__fadeInLeft">
          <h1 className="text-4xl md:text-6xl font-bold leading-tight">
            Revolutionary <span className="text-blue-500">Attendance Tracking</span> System
          </h1>
          <p className="text-xl text-gray-300">
            Secure, automated attendance tracking using facial recognition and IP-based verification. Eliminate proxy attendance and streamline your university's attendance management.
          </p>
          <div className="flex flex-wrap gap-4">
            <button className="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-lg font-semibold transition-colors animate__animated animate__pulse animate__infinite">
              Get Started
            </button>
            <button className="border border-blue-600 hover:bg-blue-600/10 text-white px-8 py-3 rounded-lg font-semibold transition-colors">
              Learn More
            </button>
          </div>
          <div className="flex items-center gap-8 text-gray-300">
            <div className="flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>99.9% Accuracy</span>
            </div>
            <div className="flex items-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
              <span>Secure & Private</span>
            </div>
          </div>
        </div>
        <div className="lg:w-1/2 mt-12 lg:mt-0 animate__animated animate__fadeInRight">
          <div className="relative">
            <div className="absolute -inset-1 bg-gradient-to-r from-blue-600 to-purple-600 rounded-lg blur opacity-30"></div>
            <div className="relative bg-neutral-800 p-8 rounded-lg">
              <div className="aspect-video bg-neutral-700 rounded-lg flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-24 w-24 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" />
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
<FeatureSection/>



<section id="howitworks" className="py-20 bg-neutral-900 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold mb-4">How It Works</h2>
          <p className="text-xl text-gray-400">
            Simple, secure, and efficient attendance tracking in three easy steps
          </p>
        </div>

        <div className="flex flex-col space-y-24">
          {[
            {
              id: 1,
              title: "Student Registration",
              description:
                "Students register with their university credentials and complete a one-time facial recognition setup. The system securely stores biometric data for future verification.",
            },
            {
              id: 2,
              title: "Dual Verification",
              description:
                "During each class, students verify their attendance through facial recognition while the system simultaneously checks their IP address to confirm campus presence.",
            },
            {
              id: 3,
              title: "Automated Reporting",
              description:
                "The system generates real-time attendance reports, analytics, and notifications for students and faculty. Track attendance patterns and receive instant alerts for any irregularities.",
            },
          ].map((step) => (
            <div
              key={step.id}
              className={`flex flex-col md:flex-row ${
                step.id === 2 ? "md:flex-row-reverse" : "md:flex-row"
              } items-center gap-12`}
            >
              {/* Text Section */}
              <div className={`md:w-1/2 text-center md:text-left`}>
                <h3 className="text-3xl font-semibold mb-4 text-blue-400">
                  {step.title}
                </h3>
                <p className="text-gray-300 text-lg leading-relaxed">
                  {step.description}
                </p>
              </div>

              {/* Graphic Section */}
              <div className="md:w-1/2 flex justify-center">
                <div className="relative w-full max-w-md">
                  <div className="absolute -inset-1 bg-gradient-to-r from-blue-600 to-purple-600 rounded-lg blur opacity-50"></div>
                  <div className="bg-neutral-800 p-8 rounded-xl shadow-lg relative">
                    <span className="absolute -left-6 -top-6 w-14 h-14 bg-blue-600 rounded-full flex items-center justify-center text-2xl font-bold text-white">
                      {step.id}
                    </span>
                    <div className="aspect-video bg-neutral-700 rounded-lg flex items-center justify-center">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-24 w-24 text-gray-400"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z"
                        />
                      </svg>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>












    <section id="benefits" className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-gray-900 mb-4">Benefits</h2>
          <p className="text-xl text-gray-600">
            Transform your institution's attendance management system
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-12">
          {/* Benefit 1 */}
          <div className="group hover:bg-blue-50 p-6 rounded-xl transition-all duration-300">
            <div className="w-14 h-14 bg-blue-100 rounded-lg flex items-center justify-center mb-6">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-8 w-8 text-blue-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
            <h3 className="text-xl font-semibold mb-3 text-gray-900">
              Time Saving
            </h3>
            <p className="text-gray-600">
              Reduce administrative workload by 90% with automated attendance
              tracking and report generation.
            </p>
            <ul className="mt-4 space-y-2 text-gray-600">
              <li className="flex items-center">
                <svg
                  className="w-4 h-4 mr-2 text-blue-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                Instant attendance recording
              </li>
              <li className="flex items-center">
                <svg
                  className="w-4 h-4 mr-2 text-blue-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                Automated report generation
              </li>
            </ul>
          </div>

          {/* Benefit 2 */}
          <div className="group hover:bg-blue-50 p-6 rounded-xl transition-all duration-300">
            <div className="w-14 h-14 bg-blue-100 rounded-lg flex items-center justify-center mb-6">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-8 w-8 text-blue-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"
                />
              </svg>
            </div>
            <h3 className="text-xl font-semibold mb-3 text-gray-900">
              Enhanced Security
            </h3>
            <p className="text-gray-600">
              Eliminate proxy attendance completely with dual verification
              system.
            </p>
            <ul className="mt-4 space-y-2 text-gray-600">
              <li className="flex items-center">
                <svg
                  className="w-4 h-4 mr-2 text-blue-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                Facial recognition verification
              </li>
              <li className="flex items-center">
                <svg
                  className="w-4 h-4 mr-2 text-blue-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                IP-based location tracking
              </li>
            </ul>
          </div>

          {/* Benefit 3 */}
          <div className="group hover:bg-blue-50 p-6 rounded-xl transition-all duration-300">
            <div className="w-14 h-14 bg-blue-100 rounded-lg flex items-center justify-center mb-6">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-8 w-8 text-blue-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                />
              </svg>
            </div>
            <h3 className="text-xl font-semibold mb-3 text-gray-900">
              Data Analytics
            </h3>
            <p className="text-gray-600">
              Comprehensive insights into attendance patterns and student
              engagement.
            </p>
            <ul className="mt-4 space-y-2 text-gray-600">
              <li className="flex items-center">
                <svg
                  className="w-4 h-4 mr-2 text-blue-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                Real-time analytics dashboard
              </li>
              <li className="flex items-center">
                <svg
                  className="w-4 h-4 mr-2 text-blue-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                Customizable reports
              </li>
            </ul>
          </div>
        </div>

        <div className="mt-16 text-center">
          <button className="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-lg font-semibold transition-colors">
            Get Started Now
          </button>
        </div>
      </div>
    </section>









    <section id="security" className="py-24 bg-gray-900 text-white relative">
      <div className="max-w-7xl mx-auto px-6 lg:px-8 relative z-10">
        <div className="text-center mb-16">
          <h2 className="text-5xl font-extrabold mb-4 text-gray-200">Enterprise-Grade Security</h2>
          <p className="text-lg text-gray-400">Your data security is our top priority</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          {/* Security Features */}
          <div className="space-y-8">
            {["End-to-End Encryption", "Biometric Data Protection", "Multi-Factor Authentication"].map((title, index) => (
              <div
                key={index}
                className="bg-neutral-800/90 p-6 rounded-xl border border-neutral-700 hover:border-blue-500 transition-all shadow-md flex items-start space-x-6 hover:scale-[1.02] hover:shadow-lg duration-300"
              >
                <div className="bg-blue-600/80 p-3 rounded-lg text-white shadow-md">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                  </svg>
                </div>
                <div>
                  <h3 className="text-xl font-semibold mb-1 text-gray-200">{title}</h3>
                  <p className="text-gray-400">We ensure {title} with advanced security measures.</p>
                </div>
              </div>
            ))}
          </div>

          {/* Security Certifications */}
          <div className="relative">
            <div className="relative bg-neutral-800/90 p-10 rounded-xl shadow-lg border border-neutral-700">
              <h3 className="text-2xl font-bold text-center mb-6 text-gray-200">Security Certifications</h3>
              <div className="grid grid-cols-2 gap-6">
                {["ISO 27001", "GDPR", "SOC 2", "256-bit SSL"].map((cert, index) => (
                  <div
                    key={index}
                    className="bg-neutral-700/80 p-5 rounded-xl text-center shadow-md border border-neutral-600 hover:bg-blue-700/80 transition-all cursor-pointer hover:scale-105 duration-300"
                  >
                    <span className="text-lg font-semibold text-gray-200">{cert}</span>
                    <p className="text-sm text-gray-400 mt-2">Certified & Compliant</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>








    <section className="flex justify-center items-center min-h-[70vh] bg-gray-100 p-6">
  <div className="w-full max-w-2xl space-y-4">
    <div className="collapse collapse-plus bg-white border border-gray-300 rounded-lg shadow-md">
      <input type="radio" name="faq-accordion" defaultChecked />
      <div className="collapse-title font-semibold text-lg">What is ZAPIS?</div>
      <div className="collapse-content text-gray-600 text-sm">
        ZAPIS is an IP-based facial recognition system for secure and automated attendance tracking.
      </div>
    </div>

    <div className="collapse collapse-plus bg-white border border-gray-300 rounded-lg shadow-md">
      <input type="radio" name="faq-accordion" />
      <div className="collapse-title font-semibold text-lg">How secure is ZAPIS?</div>
      <div className="collapse-content text-gray-600 text-sm">
        It uses AES-256 encryption, biometric authentication, and complies with GDPR for data protection.
      </div>
    </div>

    <div className="collapse collapse-plus bg-white border border-gray-300 rounded-lg shadow-md">
      <input type="radio" name="faq-accordion" />
      <div className="collapse-title font-semibold text-lg">Can ZAPIS integrate with HR and payroll systems?</div>
      <div className="collapse-content text-gray-600 text-sm">
        Yes, it offers APIs and webhooks for seamless integration with HRMS and payroll software.
      </div>
    </div>

    <div className="collapse collapse-plus bg-white border border-gray-300 rounded-lg shadow-md">
      <input type="radio" name="faq-accordion" />
      <div className="collapse-title font-semibold text-lg">Does ZAPIS work offline?</div>
      <div className="collapse-content text-gray-600 text-sm">
        It primarily operates online but supports offline mode with local storage and syncs data when reconnected.
      </div>
    </div>

    <div className="collapse collapse-plus bg-white border border-gray-300 rounded-lg shadow-md">
      <input type="radio" name="faq-accordion" />
      <div className="collapse-title font-semibold text-lg">What are the hardware requirements?</div>
      <div className="collapse-content text-gray-600 text-sm">
        Requires an IP camera, a compatible server (cloud/on-premise), and internet connectivity.
      </div>
    </div>
  </div>
</section>


<AnimatedTestimonialsDemo/>




<footer className="bg-gray-900 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="py-12 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Company Info */}
          <div className="space-y-6">
            <h2 className="text-2xl font-bold">Zapis</h2>
            <p className="text-gray-400">Revolutionary attendance tracking system for modern educational institutions.</p>
            <div className="flex space-x-4">
              <a href="#" className="text-gray-400 hover:text-white transition-colors">
                <span className="sr-only">Facebook</span>
                <svg className="h-6 w-6" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 12.073c0-6.627-5.373-12-12-12S0 5.373 0 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
                </svg>
              </a>
              <a href="#" className="text-gray-400 hover:text-white transition-colors">
                <span className="sr-only">Twitter</span>
                <svg className="h-6 w-6" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z" />
                </svg>
              </a>
            </div>
          </div>
          {/* Quick Links */}
          <div>
            <h3 className="text-lg font-semibold mb-4">Quick Links</h3>
            <ul className="space-y-3">
              <li><a href="#features" className="text-gray-400 hover:text-white transition-colors">Features</a></li>
              <li><a href="#howitworks" className="text-gray-400 hover:text-white transition-colors">How It Works</a></li>
              <li><a href="#pricing" className="text-gray-400 hover:text-white transition-colors">Pricing</a></li>
              <li><a href="#contact" className="text-gray-400 hover:text-white transition-colors">Contact</a></li>
            </ul>
          </div>
          {/* Legal */}
          <div>
            <h3 className="text-lg font-semibold mb-4">Legal</h3>
            <ul className="space-y-3">
              <li><a href="#" className="text-gray-400 hover:text-white transition-colors">Privacy Policy</a></li>
              <li><a href="#" className="text-gray-400 hover:text-white transition-colors">Terms of Service</a></li>
              <li><a href="#" className="text-gray-400 hover:text-white transition-colors">Cookie Policy</a></li>
              <li><a href="#" className="text-gray-400 hover:text-white transition-colors">GDPR Compliance</a></li>
            </ul>
          </div>
          {/* Contact */}
          <div>
            <h3 className="text-lg font-semibold mb-4">Contact</h3>
            <ul className="space-y-3">
              <li className="flex items-center text-gray-400">support@zapis.com</li>
              <li className="flex items-center text-gray-400">+1 (555) 123-4567</li>
            </ul>
          </div>
        </div>
        {/* Bottom Bar */}
        <div className="border-t border-gray-800 py-8 mt-12 flex flex-col md:flex-row justify-between items-center">
          <div className="text-gray-400 text-sm">© 2024 Zapis. All rights reserved.</div>
        </div>
      </div>
    </footer>






  </div>
  );
}
