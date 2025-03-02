package Zapis_Plateform.service;

import Zapis_Plateform.entity.StudentTimeTable;
import Zapis_Plateform.entity.FacultyTimeTable;
import Zapis_Plateform.entity.FacultyDetails;
import Zapis_Plateform.repository.StudentTimeTableRepository;
import Zapis_Plateform.repository.FacultyTimeTableRepository;
import Zapis_Plateform.repository.FacultyDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentTimeTableService {

    @Autowired
    private StudentTimeTableRepository studentTimeTableRepository;

    @Autowired
    private FacultyTimeTableRepository facultyTimeTableRepository;

    @Autowired
    private FacultyDetailsRepository facultyDetailsRepository;

    @Transactional
    public void generateStudentTimeTable(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        List<FacultyTimeTable> facultyTimeTables = facultyTimeTableRepository.findByUsername(username);
        studentTimeTableRepository.deleteByUsername(username);

        for (FacultyTimeTable facultyTimeTable : facultyTimeTables) {
            Optional<FacultyDetails> facultyDetailsOpt = facultyDetailsRepository
                    .findByRegistrationIdAndUsername(facultyTimeTable.getRegistrationId(), username);

            if (facultyDetailsOpt.isEmpty()) {
                continue; // Log this if needed
            }

            FacultyDetails facultyDetails = facultyDetailsOpt.get();

            StudentTimeTable studentTimeTable = StudentTimeTable.builder()
                    .course(facultyTimeTable.getCourse())
                    .specialisation(facultyTimeTable.getSpecialisation())
                    .batch(facultyTimeTable.getBatch())
                    .semester(facultyTimeTable.getSemester()) // Include semester from faculty timetable
                    .subject(facultyTimeTable.getSubject())
                    .roomNo(facultyTimeTable.getRoomNo())
                    .date(facultyTimeTable.getDate()) // Use the already formatted date
                    .startTimeEndTime(facultyTimeTable.getStartTimeEndTime())
                    .facultyName(facultyDetails.getName()) // Use faculty name instead of collegeEmail
                    .username(username)
                    .apoUser(facultyTimeTable.getApoUser())
                    .build();

            studentTimeTableRepository.save(studentTimeTable);
        }
    }

    public List<StudentTimeTable> getStudentTimeTableByCourseAndBatch(String course, String batch, String username) {
        if (course == null || batch == null || username == null) {
            throw new IllegalArgumentException("Course, batch, and username cannot be null");
        }
        return studentTimeTableRepository.findByCourseAndBatchAndUsername(course, batch, username);
    }

    public List<StudentTimeTable> getStudentTimeTableByCourseAndSpecialisationAndBatch(
            String course, String specialisation, String batch, String username) {
        if (course == null || batch == null || username == null) {
            throw new IllegalArgumentException("Course, batch, and username cannot be null");
        }
        return studentTimeTableRepository.findByCourseAndSpecialisationAndBatchAndUsername(course, specialisation, batch, username);
    }

    public List<StudentTimeTable> getAllStudentTimeTablesByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return studentTimeTableRepository.findByUsername(username);
    }
}