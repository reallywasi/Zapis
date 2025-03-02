package Zapis_Plateform.dto;

import Zapis_Plateform.entity.StudentDetails;
import Zapis_Plateform.entity.StudentTimeTable;
import lombok.Data;

import java.util.List;

@Data
public class StudentDashboardResponse {
    private StudentDetails personalDetails;
    private String profileImagePath;
    private List<StudentTimeTable> currentDayTimetable;
    private StudentTimeTable currentClass;
    private String message;
    private String ipAddress;
    private String classCode;
}