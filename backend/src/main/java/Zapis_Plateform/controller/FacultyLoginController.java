package Zapis_Plateform.controller;

import Zapis_Plateform.dto.AuthRequest;
import Zapis_Plateform.dto.FacultyDashboardResponse;
import Zapis_Plateform.entity.FacultyDetails;
import Zapis_Plateform.service.FacultyDetailsService;
import Zapis_Plateform.service.FacultyDashboardService;
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
@RequestMapping("/api/login/faculty")
public class FacultyLoginController {

    @Autowired
    private FacultyDetailsService facultyDetailsService;

    @Autowired
    private FacultyDashboardService facultyDashboardService;

    @Autowired
    private RICDetailsService ricDetailsService;

    @Autowired
    private SessionManager sessionManager;

    @PostMapping
    public ResponseEntity<?> loginFaculty(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        if (sessionManager.isFacultyLoggedIn(authRequest.getUsername())) {
            return ResponseEntity.status(400).body("Faculty already logged in!");
        }

        Optional<FacultyDetails> facultyOptional = facultyDetailsService.getFacultyDetailsByCollegeEmail(authRequest.getUsername());

        if (facultyOptional.isPresent()) {
            FacultyDetails faculty = facultyOptional.get();
            if (!faculty.getSap().equals(authRequest.getPassword())) {
                return ResponseEntity.status(401).body("Invalid SAP password!");
            }

            sessionManager.loginFaculty(authRequest.getUsername());
            FacultyDashboardResponse dashboardData = facultyDashboardService.getFacultyDashboardData(authRequest.getUsername());
            String ipAddress = getLocalIpAddress();
            dashboardData.setIpAddress(ipAddress != null ? ipAddress : getClientIp(request));

            // Fetch classCode from ric_details based on IP and APO user
            if (ipAddress != null) {
                String apoUsername = faculty.getUsername(); // Use faculty's username (linked to APO, excluding apoUser details)
                Optional<String> classCode = ricDetailsService.getClassCodeByIpAddressAndUsername(ipAddress, apoUsername);
                dashboardData.setClassCode(classCode.orElse(null));
            }

            return ResponseEntity.ok(dashboardData);
        }
        return ResponseEntity.status(404).body("Faculty not found!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutFaculty(@RequestBody AuthRequest authRequest) {
        if (!sessionManager.isFacultyLoggedIn(authRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Faculty is not logged in!");
        }
        sessionManager.logoutFaculty(authRequest.getUsername());
        return ResponseEntity.ok("Faculty logged out successfully!");
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