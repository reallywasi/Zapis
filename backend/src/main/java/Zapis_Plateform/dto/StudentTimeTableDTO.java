package Zapis_Plateform.dto;

import lombok.Data;

@Data
public class StudentTimeTableDTO {
    private Long id;
    private String course;
    private String specialisation;
    private String batch;
    private String subject;
    private String roomNo;
    private String date;
    private String startTimeEndTime;
    private String facultyName;  
    private String username;
    private String semester;
}