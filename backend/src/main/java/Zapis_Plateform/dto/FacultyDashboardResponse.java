package Zapis_Plateform.dto;

import Zapis_Plateform.entity.FacultyDetails;
import Zapis_Plateform.entity.FacultyTimeTable;
import lombok.Data;

import java.util.List;

@Data
public class FacultyDashboardResponse {
    private FacultyDetails personalDetails;
    private String profileImagePath;
    private List<FacultyTimeTable> currentDayTimetable;
    private FacultyTimeTable currentClass;
    private String message;
    private String ipAddress;
    private String classCode;
}