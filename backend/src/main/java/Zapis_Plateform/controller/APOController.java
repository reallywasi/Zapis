package Zapis_Plateform.controller;

import Zapis_Plateform.dto.*;
import Zapis_Plateform.entity.*;
import Zapis_Plateform.service.*;
import Zapis_Plateform.utils.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apo")
public class APOController {

    @Autowired
    private APOService apoService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private StudentDetailsService studentDetailsService;

    @Autowired
    private FacultyDetailsService facultyDetailsService;

    @Autowired
    private RICDetailsService ricDetailsService;

    @Autowired
    private FacultyTimeTableService facultyTimeTableService;

    @Autowired
    private StudentTimeTableService studentTimeTableService;

    @PostMapping("/save")
    public ResponseEntity<?> saveApoData(@RequestBody APORequest request) {
        if (!sessionManager.isAPOLoggedIn(request.getUsername())) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        APO_Dashboard savedData = apoService.saveAPOData(request);
        return ResponseEntity.ok(savedData);
    }

    @GetMapping("/data")
    public ResponseEntity<?> getApoData(@RequestParam String username) {
        if (!sessionManager.isAPOLoggedIn(username)) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        Optional<APO_Dashboard> apoData = apoService.getAPODataByUsername(username);
        return ResponseEntity.ok(apoData);
    }

    @GetMapping("/registrationData")
    public ResponseEntity<?> getApoRegistrationData(@RequestParam String username) {
        if (!sessionManager.isAPOLoggedIn(username)) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/uploadStudentDetails")
    public ResponseEntity<?> uploadStudentDetails(@RequestParam("file") MultipartFile file) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return ResponseEntity.badRequest().body("Only .xlsx files are allowed!");
        }
        try {
            List<String> duplicateMessages = studentDetailsService.saveStudentDetailsFromExcel(file, username);
            return ResponseEntity.ok(duplicateMessages);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing the file: " + e.getMessage());
        }
    }

    @GetMapping("/getStudentDetails")
    public ResponseEntity<?> getStudentDetails() {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        List<StudentDetails> studentDetails = studentDetailsService.getStudentDetailsByUsername(username);
        List<StudentDetailsDTO> studentDetailsDTOs = studentDetails.stream().map(this::mapToStudentDetailsDTO).collect(Collectors.toList());
        return ResponseEntity.ok(studentDetailsDTOs);
    }

    @PutMapping("/updateStudentDetails/{id}")
    public ResponseEntity<?> updateStudentDetails(@PathVariable Long id, @RequestBody StudentDetails updatedDetails) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        StudentDetails updatedStudent = studentDetailsService.updateStudentDetails(id, updatedDetails, username);
        return ResponseEntity.ok(mapToStudentDetailsDTO(updatedStudent));
    }

    @DeleteMapping("/deleteStudentDetails/{id}")
    public ResponseEntity<?> deleteStudentDetails(@PathVariable Long id) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        studentDetailsService.deleteStudentDetails(id, username);
        return ResponseEntity.ok("Student detail deleted successfully.");
    }

    @PostMapping("/uploadFacultyDetails")
    public ResponseEntity<?> uploadFacultyDetails(@RequestParam("file") MultipartFile file) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return ResponseEntity.badRequest().body("Only .xlsx files are allowed!");
        }
        try {
            List<String> duplicateMessages = facultyDetailsService.saveFacultyDetailsFromExcel(file, username);
            return ResponseEntity.ok(duplicateMessages);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing the file: " + e.getMessage());
        }
    }

    @GetMapping("/getFacultyDetails")
    public ResponseEntity<?> getFacultyDetails() {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        List<FacultyDetails> facultyDetails = facultyDetailsService.getFacultyDetailsByUsername(username);
        List<FacultyDetailsDTO> facultyDetailsDTOs = facultyDetails.stream().map(this::mapToFacultyDetailsDTO).collect(Collectors.toList());
        return ResponseEntity.ok(facultyDetailsDTOs);
    }

    @PutMapping("/updateFacultyDetails/{id}")
    public ResponseEntity<?> updateFacultyDetails(@PathVariable Long id, @RequestBody FacultyDetails updatedDetails) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        FacultyDetails updatedFaculty = facultyDetailsService.updateFacultyDetails(id, updatedDetails, username);
        return ResponseEntity.ok(mapToFacultyDetailsDTO(updatedFaculty));
    }

    @DeleteMapping("/deleteFacultyDetails/{id}")
    public ResponseEntity<?> deleteFacultyDetails(@PathVariable Long id) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        facultyDetailsService.deleteFacultyDetails(id, username);
        return ResponseEntity.ok("Faculty detail deleted successfully.");
    }

    @PostMapping("/uploadRicDetails")
    public ResponseEntity<?> uploadRicDetails(@RequestParam("file") MultipartFile file) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return ResponseEntity.badRequest().body("Only .xlsx files are allowed!");
        }
        try {
            List<String> duplicateMessages = ricDetailsService.saveRICDetailsFromExcel(file, username);
            return ResponseEntity.ok(duplicateMessages);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing the file: " + e.getMessage());
        }
    }

    @GetMapping("/getRicDetails")
    public ResponseEntity<?> getRicDetails() {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        List<RICDetails> ricDetails = ricDetailsService.getRICDetailsByUsername(username);
        List<RICDetailsDTO> ricDetailsDTOs = ricDetails.stream().map(this::mapToRICDetailsDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ricDetailsDTOs);
    }

    @PutMapping("/updateRicDetails/{id}")
    public ResponseEntity<?> updateRicDetails(@PathVariable Long id, @RequestBody RICDetails updatedDetails) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        RICDetails updatedRIC = ricDetailsService.updateRICDetails(id, updatedDetails, username);
        return ResponseEntity.ok(mapToRICDetailsDTO(updatedRIC));
    }

    @DeleteMapping("/deleteRicDetails/{id}")
    public ResponseEntity<?> deleteRicDetails(@PathVariable Long id) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        ricDetailsService.deleteRICDetails(id, username);
        return ResponseEntity.ok("RIC detail deleted successfully.");
    }

    @PostMapping("/uploadFacultyTimeTable")
    public ResponseEntity<?> uploadFacultyTimeTable(@RequestParam("file") MultipartFile file) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return ResponseEntity.badRequest().body("Only .xlsx files are allowed!");
        }
        try {
            List<String> duplicateMessages = facultyTimeTableService.saveFacultyTimeTableFromExcel(file, username);
            return ResponseEntity.ok(duplicateMessages);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing the file: " + e.getMessage());
        }
    }

    @GetMapping("/getFacultyTimeTable")
    public ResponseEntity<?> getFacultyTimeTable() {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        List<FacultyTimeTable> facultyTimeTable = facultyTimeTableService.getFacultyTimeTableByUsername(username);
        List<FacultyTimeTableDTO> facultyTimeTableDTOs = facultyTimeTable.stream().map(this::mapToFacultyTimeTableDTO).collect(Collectors.toList());
        return ResponseEntity.ok(facultyTimeTableDTOs);
    }

    @PutMapping("/updateFacultyTimeTable/{id}")
    public ResponseEntity<?> updateFacultyTimeTable(@PathVariable Long id, @RequestBody FacultyTimeTable updatedDetails) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        FacultyTimeTable updatedTimeTable = facultyTimeTableService.updateFacultyTimeTable(id, updatedDetails, username);
        return ResponseEntity.ok(mapToFacultyTimeTableDTO(updatedTimeTable));
    }

    @DeleteMapping("/deleteSingleFacultyTimeTable/{id}")
    public ResponseEntity<?> deleteSingleFacultyTimeTable(@PathVariable Long id) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        facultyTimeTableService.deleteFacultyTimeTable(id, username);
        return ResponseEntity.ok("Class schedule deleted successfully.");
    }

    @DeleteMapping("/deleteFullFacultyTimeTable/{registrationId}")
    public ResponseEntity<?> deleteFullFacultyTimeTable(@PathVariable String registrationId) {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        facultyTimeTableService.deleteFacultyTimeTableByRegistrationId(registrationId, username);
        return ResponseEntity.ok("Full timetable for Registration ID " + registrationId + " deleted successfully.");
    }

    @GetMapping("/getStudentTimeTable")
    public ResponseEntity<?> getStudentTimeTable() {
        String username = sessionManager.getLoggedInAPO();
        if (username == null) {
            return ResponseEntity.status(401).body("APO not logged in!");
        }
        List<StudentTimeTable> studentTimeTable = studentTimeTableService.getAllStudentTimeTablesByUsername(username);
        List<StudentTimeTableDTO> studentTimeTableDTOs = studentTimeTable.stream().map(this::mapToStudentTimeTableDTO).collect(Collectors.toList());
        return ResponseEntity.ok(studentTimeTableDTOs);
    }

    // Mapping methods
    private StudentDetailsDTO mapToStudentDetailsDTO(StudentDetails student) {
        StudentDetailsDTO dto = new StudentDetailsDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setSap(student.getSap());
        dto.setRegistrationId(student.getRegistrationId());
        dto.setCollegeEmail(student.getCollegeEmail());
        dto.setCourse(student.getCourse());
        dto.setDepartment(student.getDepartment());
        dto.setSemester(student.getSemester());
        dto.setBatch(student.getBatch());
        dto.setUsername(student.getUsername());
        dto.setProfileImagePath(student.getProfileImagePath());
        return dto;
    }

    private FacultyDetailsDTO mapToFacultyDetailsDTO(FacultyDetails faculty) {
        FacultyDetailsDTO dto = new FacultyDetailsDTO();
        dto.setId(faculty.getId());
        dto.setName(faculty.getName());
        dto.setSap(faculty.getSap());
        dto.setRegistrationId(faculty.getRegistrationId());
        dto.setCollegeEmail(faculty.getCollegeEmail());
        dto.setDepartment(faculty.getDepartment());
        dto.setUsername(faculty.getUsername());
        dto.setProfileImagePath(faculty.getProfileImagePath());
        return dto;
    }

    private RICDetailsDTO mapToRICDetailsDTO(RICDetails ric) {
        RICDetailsDTO dto = new RICDetailsDTO();
        dto.setId(ric.getId());
        dto.setRoomNo(ric.getRoomNo());
        dto.setIpAddress(ric.getIpAddress());
        dto.setClassCode(ric.getClassCode());
        dto.setUsername(ric.getUsername());
        return dto;
    }

    private FacultyTimeTableDTO mapToFacultyTimeTableDTO(FacultyTimeTable timetable) {
        FacultyTimeTableDTO dto = new FacultyTimeTableDTO();
        dto.setId(timetable.getId());
        dto.setRegistrationId(timetable.getRegistrationId());
        dto.setCourse(timetable.getCourse());
        dto.setSpecialisation(timetable.getSpecialisation());
        dto.setBatch(timetable.getBatch());
        dto.setSubject(timetable.getSubject());
        dto.setRoomNo(timetable.getRoomNo());
        dto.setDate(timetable.getDate());
        dto.setStartTimeEndTime(timetable.getStartTimeEndTime());
        dto.setUsername(timetable.getUsername());
        dto.setSemester(timetable.getSemester());
        dto.setFacultyName(timetable.getFacultyName());
        return dto;
    }

    private StudentTimeTableDTO mapToStudentTimeTableDTO(StudentTimeTable timetable) {
        StudentTimeTableDTO dto = new StudentTimeTableDTO();
        dto.setId(timetable.getId());
        dto.setCourse(timetable.getCourse());
        dto.setSpecialisation(timetable.getSpecialisation());
        dto.setBatch(timetable.getBatch());
        dto.setSubject(timetable.getSubject());
        dto.setRoomNo(timetable.getRoomNo());
        dto.setDate(timetable.getDate());
        dto.setStartTimeEndTime(timetable.getStartTimeEndTime());
        dto.setFacultyName(timetable.getFacultyName());
        dto.setUsername(timetable.getUsername());
        dto.setSemester(timetable.getSemester());
        return dto;
    }
}