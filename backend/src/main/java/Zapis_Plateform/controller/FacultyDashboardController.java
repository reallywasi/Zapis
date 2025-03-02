package Zapis_Plateform.controller;

import Zapis_Plateform.dto.FacultyDashboardResponse;
import Zapis_Plateform.entity.Faculty_Dashboard;
import Zapis_Plateform.service.FacultyDashboardService;
import Zapis_Plateform.utils.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@RestController
@RequestMapping("/api/faculty/dashboard")
public class FacultyDashboardController {

    @Autowired
    private FacultyDashboardService facultyDashboardService;

    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/save")
    public ResponseEntity<?> saveDashboardData(@RequestParam String username, @RequestBody String data) {
        if (!sessionManager.isFacultyLoggedIn(username)) {
            return ResponseEntity.status(401).body("Faculty not logged in!");
        }
        Faculty_Dashboard savedData = facultyDashboardService.saveDashboardData(username, data);
        return ResponseEntity.ok(savedData);
    }

    @GetMapping("/data")
    public ResponseEntity<?> getDashboardData(HttpServletRequest request) {
        String username = sessionManager.getLoggedInFaculty();
        if (username == null || !sessionManager.isFacultyLoggedIn(username)) {
            return ResponseEntity.status(401).body("Faculty not logged in!");
        }
        FacultyDashboardResponse dashboardData = facultyDashboardService.getFacultyDashboardData(username);
        String ipAddress = getLocalIpAddress();
        dashboardData.setIpAddress(ipAddress != null ? ipAddress : getClientIp(request));
        return ResponseEntity.ok(dashboardData);
    }

    @PostMapping("/markAttendance")
    public ResponseEntity<?> markAttendance(HttpServletRequest request) {
        String username = sessionManager.getLoggedInFaculty();
        if (username == null || !sessionManager.isFacultyLoggedIn(username)) {
            return ResponseEntity.status(401).body("Faculty not logged in!");
        }
        String ipAddress = getLocalIpAddress();
        if (ipAddress == null) {
            return ResponseEntity.badRequest().body("Unable to determine IP address!");
        }
        try {
            String result = facultyDashboardService.markAttendance(username, ipAddress);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getAttendanceSummary")
    public ResponseEntity<?> getAttendanceSummary() {
        String username = sessionManager.getLoggedInFaculty();
        if (username == null || !sessionManager.isFacultyLoggedIn(username)) {
            return ResponseEntity.status(401).body("Faculty not logged in!");
        }
        return ResponseEntity.ok(facultyDashboardService.getAttendanceSummary(username));
    }

    @GetMapping("/getFullTimetable")
    public ResponseEntity<?> getFullTimetable() {
        String username = sessionManager.getLoggedInFaculty();
        if (username == null || !sessionManager.isFacultyLoggedIn(username)) {
            return ResponseEntity.status(401).body("Faculty not logged in!");
        }
        return ResponseEntity.ok(facultyDashboardService.getFullTimetable(username));
    }

    @PostMapping("/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String loggedInFaculty = sessionManager.getLoggedInFaculty();
        if (loggedInFaculty == null || !sessionManager.isFacultyLoggedIn(loggedInFaculty)) {
            return ResponseEntity.status(401).body("Faculty not logged in!");
        }
        try {
            String message = facultyDashboardService.uploadProfileImage(loggedInFaculty, file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload profile image: " + e.getMessage());
        }
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