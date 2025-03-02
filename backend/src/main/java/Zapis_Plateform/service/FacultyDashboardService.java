package Zapis_Plateform.service;

import Zapis_Plateform.dto.AttendanceSummary;
import Zapis_Plateform.dto.FacultyDashboardResponse;
import Zapis_Plateform.entity.*;
import Zapis_Plateform.repository.*;
import Zapis_Plateform.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyDashboardService {

    @Autowired
    private FacultyDashboardRepository facultyDashboardRepository;

    @Autowired
    private FacultyDetailsRepository facultyDetailsRepository;

    @Autowired
    private FacultyTimeTableRepository facultyTimeTableRepository;

    @Autowired
    private FacultyAttendanceRepository facultyAttendanceRepository;

    @Autowired
    private RICDetailsService ricDetailsService;

    public Faculty_Dashboard saveDashboardData(String facultyId, String data) {
        FacultyDetails faculty = facultyDetailsRepository.findById(Long.parseLong(facultyId))
                .orElseThrow(() -> new RuntimeException("Faculty not found!"));

        Faculty_Dashboard dashboard = new Faculty_Dashboard();
        dashboard.setUsername(faculty.getCollegeEmail());
        dashboard.setData(data);
        dashboard.setFaculty(faculty);

        return facultyDashboardRepository.save(dashboard);
    }

    public Optional<Faculty_Dashboard> getDashboardByUsername(String username) {
        return facultyDashboardRepository.findByUsername(username);
    }

    public FacultyDashboardResponse getFacultyDashboardData(String collegeEmail) {
        FacultyDashboardResponse response = new FacultyDashboardResponse();

        Optional<FacultyDetails> facultyOpt = facultyDetailsRepository.findByCollegeEmail(collegeEmail);
        if (facultyOpt.isEmpty()) {
            throw new RuntimeException("Faculty not found!");
        }
        FacultyDetails faculty = facultyOpt.get();
        // Remove apoUser from personalDetails by creating a DTO or filtering
        FacultyDetails filteredFaculty = new FacultyDetails();
        filteredFaculty.setId(faculty.getId());
        filteredFaculty.setName(faculty.getName());
        filteredFaculty.setSap(faculty.getSap());
        filteredFaculty.setRegistrationId(faculty.getRegistrationId());
        filteredFaculty.setCollegeEmail(faculty.getCollegeEmail());
        filteredFaculty.setDepartment(faculty.getDepartment());
        filteredFaculty.setUsername(faculty.getUsername());
        filteredFaculty.setProfileImagePath(faculty.getProfileImagePath());
        response.setPersonalDetails(filteredFaculty);

        if (faculty.getProfileImagePath() == null || faculty.getProfileImagePath().isEmpty()) {
            response.setMessage("Please upload your profile image.");
        } else {
            response.setProfileImagePath(faculty.getProfileImagePath());
        }

        LocalDate today = LocalDate.now();
        String todayStr = DateUtil.formatLocalDate(today);

        List<FacultyTimeTable> currentDayTimetable = facultyTimeTableRepository.findByRegistrationId(faculty.getRegistrationId())
                .stream()
                .map(tt -> {
                    LocalDate convertedDate = DateUtil.excelSerialToLocalDate(tt.getDate());
                    tt.setDate(DateUtil.formatLocalDate(convertedDate));
                    // Remove apoUser from currentDayTimetable
                    FacultyTimeTable filteredTT = new FacultyTimeTable();
                    filteredTT.setId(tt.getId());
                    filteredTT.setRegistrationId(tt.getRegistrationId());
                    filteredTT.setCourse(tt.getCourse());
                    filteredTT.setSpecialisation(tt.getSpecialisation());
                    filteredTT.setBatch(tt.getBatch());
                    filteredTT.setSubject(tt.getSubject());
                    filteredTT.setRoomNo(tt.getRoomNo());
                    filteredTT.setDate(tt.getDate());
                    filteredTT.setStartTimeEndTime(tt.getStartTimeEndTime());
                    filteredTT.setUsername(tt.getUsername());
                    filteredTT.setSemester(tt.getSemester());
                    filteredTT.setFacultyName(tt.getFacultyName());
                    return filteredTT;
                })
                .filter(tt -> tt.getDate().equals(todayStr))
                .sorted(Comparator.comparing(FacultyTimeTable::getStartTimeEndTime))
                .collect(Collectors.toList());

        if (currentDayTimetable.isEmpty()) {
            String message = response.getMessage() != null ? response.getMessage() + " No timetable for today." : "No timetable for today.";
            response.setMessage(message);
        }
        response.setCurrentDayTimetable(currentDayTimetable);

        LocalTime now = LocalTime.now();
        FacultyTimeTable currentClass = null;

        // Find the most recent ongoing or upcoming class
        for (FacultyTimeTable timetable : currentDayTimetable) {
            String[] times = timetable.getStartTimeEndTime().split("-");
            if (times.length == 2) {
                LocalTime start = LocalTime.parse(times[0].trim());
                LocalTime end = LocalTime.parse(times[1].trim());

                if (now.isAfter(start) && now.isBefore(end)) {
                    // Class is currently ongoing
                    currentClass = timetable;
                    break;
                } else if (now.isBefore(start)) {
                    // If no ongoing class is found, select the next upcoming class
                    if (currentClass == null || start.isBefore(LocalTime.parse(currentClass.getStartTimeEndTime().split("-")[0].trim()))) {
                        currentClass = timetable;
                    }
                }
            }
        }

        // Remove apoUser from currentClass
        if (currentClass != null) {
            FacultyTimeTable filteredCurrentClass = new FacultyTimeTable();
            filteredCurrentClass.setId(currentClass.getId());
            filteredCurrentClass.setRegistrationId(currentClass.getRegistrationId());
            filteredCurrentClass.setCourse(currentClass.getCourse());
            filteredCurrentClass.setSpecialisation(currentClass.getSpecialisation());
            filteredCurrentClass.setBatch(currentClass.getBatch());
            filteredCurrentClass.setSubject(currentClass.getSubject());
            filteredCurrentClass.setRoomNo(currentClass.getRoomNo());
            filteredCurrentClass.setDate(currentClass.getDate());
            filteredCurrentClass.setStartTimeEndTime(currentClass.getStartTimeEndTime());
            filteredCurrentClass.setUsername(currentClass.getUsername());
            filteredCurrentClass.setSemester(currentClass.getSemester());
            filteredCurrentClass.setFacultyName(currentClass.getFacultyName());
            response.setCurrentClass(filteredCurrentClass);
        }

        return response;
    }

    public String markAttendance(String username, String ipAddress) {
        FacultyDashboardResponse dashboard = getFacultyDashboardData(username);
        FacultyTimeTable currentClass = dashboard.getCurrentClass();
        if (currentClass == null) {
            throw new RuntimeException("No current class found to mark attendance!");
        }

        String apoUsername = getApoUsernameByFacultyEmail(username);
        if (apoUsername == null) {
            throw new RuntimeException("APO username not found for this faculty!");
        }

        Optional<RICDetails> ricDetails = ricDetailsService.getRICDetailsByUsername(apoUsername)
                .stream()
                .filter(ric -> ric.getIpAddress().equals(ipAddress))
                .findFirst();
        if (ricDetails.isEmpty()) {
            throw new RuntimeException("No RIC details found for IP address: " + ipAddress);
        }

        String ricClassCode = ricDetails.get().getClassCode();
        if (!ricDetails.get().getRoomNo().equals(currentClass.getRoomNo())) {
            throw new RuntimeException("Room number (" + ricDetails.get().getRoomNo() + ") does not match current class room number (" + currentClass.getRoomNo() + ")!");
        }

        Optional<FacultyAttendance> existingAttendance = facultyAttendanceRepository
                .findByFacultyUsernameAndClassCodeAndDateAndStartTimeEndTime(
                        username, ricClassCode, currentClass.getDate(), currentClass.getStartTimeEndTime());
        if (existingAttendance.isPresent()) {
            throw new RuntimeException("Attendance already marked for this class!");
        }

        LocalTime now = LocalTime.now();
        String[] times = currentClass.getStartTimeEndTime().split("-");
        LocalTime startTime = LocalTime.parse(times[0].trim());
        LocalTime deadline = startTime.plusMinutes(10);

        if (now.isBefore(startTime) || now.isAfter(deadline)) {
            throw new RuntimeException("Attendance can only be marked within 10 minutes of class start time ("
                    + startTime + " to " + deadline + "), current time: " + now);
        }

        FacultyAttendance attendance = FacultyAttendance.builder()
                .facultyUsername(username)
                .classCode(ricClassCode)
                .roomNo(currentClass.getRoomNo())
                .date(currentClass.getDate())
                .startTimeEndTime(currentClass.getStartTimeEndTime())
                .markedAt(LocalDateTime.now())
                .present(true)
                .build();

        facultyAttendanceRepository.save(attendance);
        return "Attendance marked successfully for " + currentClass.getSubject() + " at " + currentClass.getStartTimeEndTime();
    }

    public Map<String, AttendanceSummary> getAttendanceSummary(String username) {
        List<FacultyTimeTable> allClasses = facultyTimeTableRepository.findByRegistrationId(
                facultyDetailsRepository.findByCollegeEmail(username)
                        .map(FacultyDetails::getRegistrationId)
                        .orElseThrow(() -> new RuntimeException("Faculty not found!"))
        ).stream()
                .map(tt -> {
                    LocalDate convertedDate = DateUtil.excelSerialToLocalDate(tt.getDate());
                    tt.setDate(DateUtil.formatLocalDate(convertedDate));
                    // Remove apoUser from allClasses
                    FacultyTimeTable filteredTT = new FacultyTimeTable();
                    filteredTT.setId(tt.getId());
                    filteredTT.setRegistrationId(tt.getRegistrationId());
                    filteredTT.setCourse(tt.getCourse());
                    filteredTT.setSpecialisation(tt.getSpecialisation());
                    filteredTT.setBatch(tt.getBatch());
                    filteredTT.setSubject(tt.getSubject());
                    filteredTT.setRoomNo(tt.getRoomNo());
                    filteredTT.setDate(tt.getDate());
                    filteredTT.setStartTimeEndTime(tt.getStartTimeEndTime());
                    filteredTT.setUsername(tt.getUsername());
                    filteredTT.setSemester(tt.getSemester());
                    filteredTT.setFacultyName(tt.getFacultyName());
                    return filteredTT;
                })
                .collect(Collectors.toList());

        List<FacultyAttendance> attendanceRecords = facultyAttendanceRepository.findByFacultyUsername(username);

        Map<String, List<FacultyAttendance>> attendanceBySubject = attendanceRecords.stream()
                .collect(Collectors.groupingBy(att -> getSubjectByClassCodeAndTime(att.getClassCode(), att.getStartTimeEndTime(), username)));

        Map<String, List<FacultyTimeTable>> timetableBySubject = allClasses.stream()
                .collect(Collectors.groupingBy(FacultyTimeTable::getSubject));

        Map<String, AttendanceSummary> summaryMap = new HashMap<>();
        timetableBySubject.forEach((subject, classes) -> {
            AttendanceSummary summary = new AttendanceSummary();
            List<AttendanceSummary.AttendanceRecord> records = new ArrayList<>();
            int presentDays = 0;

            for (FacultyTimeTable tt : classes) {
                Optional<FacultyAttendance> attendance = attendanceRecords.stream()
                        .filter(att -> att.getDate().equals(tt.getDate()) && att.getStartTimeEndTime().equals(tt.getStartTimeEndTime()))
                        .findFirst();
                AttendanceSummary.AttendanceRecord record = new AttendanceSummary.AttendanceRecord();
                record.setDate(tt.getDate());
                record.setSubject(subject);
                record.setStartTimeEndTime(tt.getStartTimeEndTime());
                record.setPresent(attendance.isPresent() && attendance.get().isPresent());
                if (record.isPresent()) presentDays++;
                records.add(record);
            }

            summary.setRecords(records);
            summary.setTotalClasses(classes.size());
            summary.setPresentDays(presentDays);
            summary.setAttendancePercentage(summary.getTotalClasses() > 0 ? (summary.getPresentDays() * 100.0) / summary.getTotalClasses() : 0);
            summaryMap.put(subject, summary);
        });

        return summaryMap;
    }

    public List<FacultyTimeTable> getFullTimetable(String username) {
        return facultyTimeTableRepository.findByRegistrationId(
                facultyDetailsRepository.findByCollegeEmail(username)
                        .map(FacultyDetails::getRegistrationId)
                        .orElseThrow(() -> new RuntimeException("Faculty not found!"))
        ).stream()
                .map(tt -> {
                    LocalDate convertedDate = DateUtil.excelSerialToLocalDate(tt.getDate());
                    tt.setDate(DateUtil.formatLocalDate(convertedDate));
                    // Remove apoUser from fullTimetable
                    FacultyTimeTable filteredTT = new FacultyTimeTable();
                    filteredTT.setId(tt.getId());
                    filteredTT.setRegistrationId(tt.getRegistrationId());
                    filteredTT.setCourse(tt.getCourse());
                    filteredTT.setSpecialisation(tt.getSpecialisation());
                    filteredTT.setBatch(tt.getBatch());
                    filteredTT.setSubject(tt.getSubject());
                    filteredTT.setRoomNo(tt.getRoomNo());
                    filteredTT.setDate(tt.getDate());
                    filteredTT.setStartTimeEndTime(tt.getStartTimeEndTime());
                    filteredTT.setUsername(tt.getUsername());
                    filteredTT.setSemester(tt.getSemester());
                    filteredTT.setFacultyName(tt.getFacultyName());
                    return filteredTT;
                })
                .collect(Collectors.toList());
    }

    private String getSubjectByClassCodeAndTime(String classCode, String startTimeEndTime, String username) {
        return facultyTimeTableRepository.findByUsername(username).stream()
                .filter(tt -> tt.getStartTimeEndTime().equals(startTimeEndTime))
                .map(FacultyTimeTable::getSubject)
                .findFirst()
                .orElse("Unknown");
    }

    public String getApoUsernameByFacultyEmail(String email) {
        return facultyDetailsRepository.findByCollegeEmail(email)
                .map(faculty -> faculty.getUsername()) // Use faculty's username, excluding apoUser details
                .orElse(null);
    }

    public String uploadProfileImage(String collegeEmail, MultipartFile file) throws IOException {
        Optional<FacultyDetails> facultyOpt = facultyDetailsRepository.findByCollegeEmail(collegeEmail);
        if (facultyOpt.isEmpty()) {
            throw new RuntimeException("Faculty not found!");
        }
        FacultyDetails faculty = facultyOpt.get();

        String contentType = file.getContentType();
        if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("image/jpg")) {
            throw new RuntimeException("Only PNG, JPG, or JPEG files are allowed!");
        }

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        String fileName = collegeEmail.replace("@", "_") + "_profile.jpg";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        File resizedFile = new File(uploadDir, fileName);

        ImageIO.write(resizedImage, "jpg", resizedFile);

        faculty.setProfileImagePath(resizedFile.getAbsolutePath());
        facultyDetailsRepository.save(faculty);

        return "Profile image uploaded successfully!";
    }
}