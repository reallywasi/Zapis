package Zapis_Plateform.service;

import Zapis_Plateform.dto.AttendanceSummary;
import Zapis_Plateform.dto.StudentDashboardResponse;
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
public class StudentDashboardService {

    @Autowired
    private StudentDashboardRepository studentDashboardRepository;

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private StudentTimeTableRepository studentTimeTableRepository;

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private FacultyAttendanceRepository facultyAttendanceRepository;

    @Autowired
    private RICDetailsService ricDetailsService;

    @Autowired
    private FacultyDetailsRepository facultyDetailsRepository;

    public Student_Dashboard saveDashboardData(String studentId, String data) {
        StudentDetails student = studentDetailsRepository.findById(Long.parseLong(studentId))
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        Student_Dashboard dashboard = new Student_Dashboard();
        dashboard.setUsername(student.getCollegeEmail());
        dashboard.setData(data);
        dashboard.setStudent(student);

        return studentDashboardRepository.save(dashboard);
    }

    public Optional<Student_Dashboard> getDashboardByUsername(String username) {
        return studentDashboardRepository.findByUsername(username);
    }

    public StudentDashboardResponse getStudentDashboardData(String collegeEmail) {
        StudentDashboardResponse response = new StudentDashboardResponse();

        Optional<StudentDetails> studentOpt = studentDetailsRepository.findByCollegeEmail(collegeEmail);
        if (studentOpt.isEmpty()) {
            throw new RuntimeException("Student not found!");
        }
        StudentDetails student = studentOpt.get();
        // Remove apoUser from personalDetails
        StudentDetails filteredStudent = new StudentDetails();
        filteredStudent.setId(student.getId());
        filteredStudent.setName(student.getName());
        filteredStudent.setSap(student.getSap());
        filteredStudent.setRegistrationId(student.getRegistrationId());
        filteredStudent.setCollegeEmail(student.getCollegeEmail());
        filteredStudent.setCourse(student.getCourse());
        filteredStudent.setDepartment(student.getDepartment());
        filteredStudent.setSemester(student.getSemester());
        filteredStudent.setBatch(student.getBatch());
        filteredStudent.setUsername(student.getUsername());
        filteredStudent.setProfileImagePath(student.getProfileImagePath());
        response.setPersonalDetails(filteredStudent);

        if (student.getProfileImagePath() == null || student.getProfileImagePath().isEmpty()) {
            response.setMessage("Please upload your profile image.");
        } else {
            response.setProfileImagePath(student.getProfileImagePath());
        }

        LocalDate today = LocalDate.now();
        String todayStr = DateUtil.formatLocalDate(today);

        // Match timetable based on course, semester, and batch (specialisation optional)
        List<StudentTimeTable> currentDayTimetable = getStudentTimetableByStudent(student, todayStr)
                .stream()
                .map(tt -> {
                    LocalDate convertedDate = DateUtil.excelSerialToLocalDate(tt.getDate());
                    tt.setDate(DateUtil.formatLocalDate(convertedDate));
                    // Remove apoUser from currentDayTimetable
                    StudentTimeTable filteredTT = new StudentTimeTable();
                    filteredTT.setId(tt.getId());
                    filteredTT.setCourse(tt.getCourse());
                    filteredTT.setSpecialisation(tt.getSpecialisation());
                    filteredTT.setBatch(tt.getBatch());
                    filteredTT.setSubject(tt.getSubject());
                    filteredTT.setRoomNo(tt.getRoomNo());
                    filteredTT.setDate(tt.getDate());
                    filteredTT.setStartTimeEndTime(tt.getStartTimeEndTime());
                    filteredTT.setFacultyName(tt.getFacultyName());
                    filteredTT.setUsername(tt.getUsername());
                    filteredTT.setSemester(tt.getSemester());
                    return filteredTT;
                })
                .sorted(Comparator.comparing(StudentTimeTable::getStartTimeEndTime))
                .collect(Collectors.toList());

        if (currentDayTimetable.isEmpty()) {
            String message = response.getMessage() != null ? response.getMessage() + " No timetable for today." : "No timetable for today.";
            response.setMessage(message);
        }
        response.setCurrentDayTimetable(currentDayTimetable);

        LocalTime now = LocalTime.now();
        StudentTimeTable currentClass = currentDayTimetable.stream()
                .filter(tt -> {
                    String[] times = tt.getStartTimeEndTime().split("-");
                    LocalTime start = LocalTime.parse(times[0].trim());
                    LocalTime end = LocalTime.parse(times[1].trim());
                    return now.isAfter(start) && now.isBefore(end);
                })
                .findFirst()
                .orElse(null);

        if (currentClass == null) {
            currentClass = currentDayTimetable.stream()
                    .filter(tt -> {
                        String[] times = tt.getStartTimeEndTime().split("-");
                        LocalTime start = LocalTime.parse(times[0].trim());
                        return now.isBefore(start);
                    })
                    .findFirst()
                    .orElse(null);
        }
        // Remove apoUser from currentClass
        if (currentClass != null) {
            StudentTimeTable filteredCurrentClass = new StudentTimeTable();
            filteredCurrentClass.setId(currentClass.getId());
            filteredCurrentClass.setCourse(currentClass.getCourse());
            filteredCurrentClass.setSpecialisation(currentClass.getSpecialisation());
            filteredCurrentClass.setBatch(currentClass.getBatch());
            filteredCurrentClass.setSubject(currentClass.getSubject());
            filteredCurrentClass.setRoomNo(currentClass.getRoomNo());
            filteredCurrentClass.setDate(currentClass.getDate());
            filteredCurrentClass.setStartTimeEndTime(currentClass.getStartTimeEndTime());
            filteredCurrentClass.setFacultyName(currentClass.getFacultyName());
            filteredCurrentClass.setUsername(currentClass.getUsername());
            filteredCurrentClass.setSemester(currentClass.getSemester());
            response.setCurrentClass(filteredCurrentClass);
        }

        return response;
    }

    private List<StudentTimeTable> getStudentTimetableByStudent(StudentDetails student, String date) {
        String course = student.getCourse();
        String semester = student.getSemester();
        String batch = student.getBatch();
        String specialisation = student.getDepartment(); // Assuming department represents specialisation

        // Match without specialisation if it's null or empty
        if (specialisation == null || specialisation.trim().isEmpty()) {
            return studentTimeTableRepository.findByCourseAndBatchAndUsername(course, batch, student.getUsername())
                    .stream()
                    .filter(tt -> tt.getSemester().equals(semester))
                    .map(tt -> {
                        LocalDate convertedDate = DateUtil.excelSerialToLocalDate(tt.getDate());
                        tt.setDate(DateUtil.formatLocalDate(convertedDate));
                        // Remove apoUser from timetable
                        StudentTimeTable filteredTT = new StudentTimeTable();
                        filteredTT.setId(tt.getId());
                        filteredTT.setCourse(tt.getCourse());
                        filteredTT.setSpecialisation(tt.getSpecialisation());
                        filteredTT.setBatch(tt.getBatch());
                        filteredTT.setSubject(tt.getSubject());
                        filteredTT.setRoomNo(tt.getRoomNo());
                        filteredTT.setDate(tt.getDate());
                        filteredTT.setStartTimeEndTime(tt.getStartTimeEndTime());
                        filteredTT.setFacultyName(tt.getFacultyName());
                        filteredTT.setUsername(tt.getUsername());
                        filteredTT.setSemester(tt.getSemester());
                        return filteredTT;
                    })
                    .filter(tt -> date == null || tt.getDate().equals(date))
                    .collect(Collectors.toList());
        }

        // Match with specialisation
        return studentTimeTableRepository.findByCourseAndSpecialisationAndBatchAndUsername(course, specialisation, batch, student.getUsername())
                .stream()
                .filter(tt -> tt.getSemester().equals(semester))
                .map(tt -> {
                    LocalDate convertedDate = DateUtil.excelSerialToLocalDate(tt.getDate());
                    tt.setDate(DateUtil.formatLocalDate(convertedDate));
                    // Remove apoUser from timetable
                    StudentTimeTable filteredTT = new StudentTimeTable();
                    filteredTT.setId(tt.getId());
                    filteredTT.setCourse(tt.getCourse());
                    filteredTT.setSpecialisation(tt.getSpecialisation());
                    filteredTT.setBatch(tt.getBatch());
                    filteredTT.setSubject(tt.getSubject());
                    filteredTT.setRoomNo(tt.getRoomNo());
                    filteredTT.setDate(tt.getDate());
                    filteredTT.setStartTimeEndTime(tt.getStartTimeEndTime());
                    filteredTT.setFacultyName(tt.getFacultyName());
                    filteredTT.setUsername(tt.getUsername());
                    filteredTT.setSemester(tt.getSemester());
                    return filteredTT;
                })
                .filter(tt -> date == null || tt.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public String markAttendance(String username, String ipAddress) {
        StudentDashboardResponse dashboard = getStudentDashboardData(username);
        StudentTimeTable currentClass = dashboard.getCurrentClass();
        if (currentClass == null) {
            throw new RuntimeException("No current class found to mark attendance!");
        }

        String apoUsername = getApoUsernameByStudentEmail(username);
        if (apoUsername == null) {
            throw new RuntimeException("APO username not found for this student!");
        }

        Optional<RICDetails> ricDetails = ricDetailsService.getRICDetailsByUsername(apoUsername)
                .stream()
                .filter(ric -> ric.getIpAddress().equals(ipAddress))
                .findFirst();
        if (ricDetails.isEmpty()) {
            throw new RuntimeException("IP address (" + ipAddress + ") does not match any registered classroom!");
        }

        String ricClassCode = ricDetails.get().getClassCode();
        if (!ricDetails.get().getRoomNo().equals(currentClass.getRoomNo())) {
            throw new RuntimeException("Room number (" + ricDetails.get().getRoomNo() + ") does not match current class room number (" + currentClass.getRoomNo() + ")!");
        }

        // Use faculty collegeEmail instead of name for lookup
        String facultyEmail = facultyDetailsRepository.findByName(currentClass.getFacultyName())
                .map(FacultyDetails::getCollegeEmail)
                .orElseThrow(() -> new RuntimeException("Faculty email not found for name: " + currentClass.getFacultyName()));

        Optional<FacultyAttendance> facultyAttendance = facultyAttendanceRepository
                .findByFacultyUsernameAndClassCodeAndDateAndStartTimeEndTime(
                        facultyEmail, ricClassCode, currentClass.getDate(), currentClass.getStartTimeEndTime());
        if (!facultyAttendance.isPresent()) {
            throw new RuntimeException("Class code " + ricClassCode + " is not activated yet! Faculty (" + facultyEmail + ") has not marked attendance for " + currentClass.getDate() + " at " + currentClass.getStartTimeEndTime());
        }

        Optional<StudentAttendance> existingAttendance = studentAttendanceRepository
                .findByStudentUsernameAndClassCodeAndDateAndStartTimeEndTime(
                        username, ricClassCode, currentClass.getDate(), currentClass.getStartTimeEndTime());
        if (existingAttendance.isPresent()) {
            throw new RuntimeException("Attendance already marked for this class!");
        }

        LocalDateTime facultyMarkedAt = facultyAttendance.get().getMarkedAt();
        LocalDateTime deadline = facultyMarkedAt.plusMinutes(10);
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(facultyMarkedAt) || now.isAfter(deadline)) {
            throw new RuntimeException("Attendance can only be marked within 10 minutes after Faculty marks ("
                    + facultyMarkedAt + " to " + deadline + "), current time: " + now);
        }

        StudentAttendance attendance = StudentAttendance.builder()
                .studentUsername(username)
                .classCode(ricClassCode)
                .roomNo(currentClass.getRoomNo())
                .date(currentClass.getDate())
                .startTimeEndTime(currentClass.getStartTimeEndTime())
                .markedAt(now)
                .present(true)
                .build();

        studentAttendanceRepository.save(attendance);
        return "Attendance marked successfully for " + currentClass.getSubject() + " at " + currentClass.getStartTimeEndTime();
    }

    public Map<String, AttendanceSummary> getAttendanceSummary(String username) {
        StudentDetails student = studentDetailsRepository.findByCollegeEmail(username)
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        List<StudentTimeTable> allClasses = getStudentTimetableByStudent(student, null) // Get all timetables for the student
                .stream()
                .collect(Collectors.toList());

        List<StudentAttendance> attendanceRecords = studentAttendanceRepository.findByStudentUsername(username);

        Map<String, List<StudentAttendance>> attendanceBySubject = attendanceRecords.stream()
                .collect(Collectors.groupingBy(att -> getSubjectByClassCodeAndTime(att.getClassCode(), att.getStartTimeEndTime(), username)));

        Map<String, List<StudentTimeTable>> timetableBySubject = allClasses.stream()
                .collect(Collectors.groupingBy(StudentTimeTable::getSubject));

        Map<String, AttendanceSummary> summaryMap = new HashMap<>();
        timetableBySubject.forEach((subject, classes) -> {
            AttendanceSummary summary = new AttendanceSummary();
            List<AttendanceSummary.AttendanceRecord> records = new ArrayList<>();
            int presentDays = 0;

            for (StudentTimeTable tt : classes) {
                Optional<StudentAttendance> attendance = attendanceRecords.stream()
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

    public List<StudentTimeTable> getFullTimetable(String username) {
        StudentDetails student = studentDetailsRepository.findByCollegeEmail(username)
                .orElseThrow(() -> new RuntimeException("Student not found!"));
        return getStudentTimetableByStudent(student, null) // Get all timetables for the student
                .stream()
                .map(tt -> {
                    LocalDate convertedDate = DateUtil.excelSerialToLocalDate(tt.getDate());
                    tt.setDate(DateUtil.formatLocalDate(convertedDate));
                    // Remove apoUser from fullTimetable
                    StudentTimeTable filteredTT = new StudentTimeTable();
                    filteredTT.setId(tt.getId());
                    filteredTT.setCourse(tt.getCourse());
                    filteredTT.setSpecialisation(tt.getSpecialisation());
                    filteredTT.setBatch(tt.getBatch());
                    filteredTT.setSubject(tt.getSubject());
                    filteredTT.setRoomNo(tt.getRoomNo());
                    filteredTT.setDate(tt.getDate());
                    filteredTT.setStartTimeEndTime(tt.getStartTimeEndTime());
                    filteredTT.setFacultyName(tt.getFacultyName());
                    filteredTT.setUsername(tt.getUsername());
                    filteredTT.setSemester(tt.getSemester());
                    return filteredTT;
                })
                .collect(Collectors.toList());
    }

    private String getSubjectByClassCodeAndTime(String classCode, String startTimeEndTime, String username) {
        StudentDetails student = studentDetailsRepository.findByCollegeEmail(username)
                .orElseThrow(() -> new RuntimeException("Student not found!"));
        return getStudentTimetableByStudent(student, null).stream()
                .filter(tt -> tt.getStartTimeEndTime().equals(startTimeEndTime))
                .map(StudentTimeTable::getSubject)
                .findFirst()
                .orElse("Unknown");
    }

    public String getApoUsernameByStudentEmail(String email) {
        return studentDetailsRepository.findByCollegeEmail(email)
                .map(student -> student.getUsername()) // Use student's username, excluding apoUser details
                .orElse(null);
    }

    public String uploadProfileImage(String collegeEmail, MultipartFile file) throws IOException {
        Optional<StudentDetails> studentOpt = studentDetailsRepository.findByCollegeEmail(collegeEmail);
        if (studentOpt.isEmpty()) {
            throw new RuntimeException("Student not found!");
        }
        StudentDetails student = studentOpt.get();

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

        student.setProfileImagePath(resizedFile.getAbsolutePath());
        studentDetailsRepository.save(student);

        return "Profile image uploaded successfully!";
    }
}