package Zapis_Plateform.controller;

import Zapis_Plateform.dto.AuthRequest;
import Zapis_Plateform.dto.StudentDashboardResponse;
import Zapis_Plateform.entity.StudentDetails;
import Zapis_Plateform.service.StudentDetailsService;
import Zapis_Plateform.service.StudentDashboardService;
import Zapis_Plateform.service.RICDetailsService;
import Zapis_Plateform.utils.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;

@RestController
@RequestMapping("/api/login/student")
public class StudentLoginController {

    @Autowired
    private StudentDetailsService studentDetailsService;

    @Autowired
    private StudentDashboardService studentDashboardService;

    @Autowired
    private RICDetailsService ricDetailsService;

    @Autowired
    private SessionManager sessionManager;

    @PostMapping
    public ResponseEntity<?> loginStudent(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        if (sessionManager.isStudentLoggedIn(authRequest.getUsername())) {
            return ResponseEntity.status(400).body("Student already logged in!");
        }

        Optional<StudentDetails> studentOptional = studentDetailsService.getStudentByCollegeEmail(authRequest.getUsername());

        if (studentOptional.isPresent()) {
            StudentDetails student = studentOptional.get();
            if (!student.getSap().equals(authRequest.getPassword())) {
                return ResponseEntity.status(401).body("Invalid SAP password!");
            }

            sessionManager.loginStudent(authRequest.getUsername());
            StudentDashboardResponse dashboardData = studentDashboardService.getStudentDashboardData(authRequest.getUsername());
            String ipAddress = getLocalIpAddress();
            dashboardData.setIpAddress(ipAddress != null ? ipAddress : getClientIp(request));

            // Fetch classCode from ric_details based on IP and APO user
            if (ipAddress != null) {
                String apoUsername = student.getUsername(); // Use student's username (linked to APO, excluding apoUser details)
                Optional<String> classCode = ricDetailsService.getClassCodeByIpAddressAndUsername(ipAddress, apoUsername);
                dashboardData.setClassCode(classCode.orElse(null));
            }

            return ResponseEntity.ok(dashboardData);
        }
        return ResponseEntity.status(404).body("Student not found!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutStudent(@RequestBody AuthRequest authRequest) {
        if (!sessionManager.isStudentLoggedIn(authRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Student is not logged in!");
        }
        sessionManager.logoutStudent(authRequest.getUsername());
        return ResponseEntity.ok("Student logged out successfully!");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isSiteLocalAddress() && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}