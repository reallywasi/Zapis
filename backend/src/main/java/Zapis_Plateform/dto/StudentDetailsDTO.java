package Zapis_Plateform.dto;

import lombok.Data;

@Data
public class StudentDetailsDTO {
    private Long id;
    private String name;
    private String sap;
    private String registrationId;
    private String collegeEmail;
    private String course;
    private String department;
    private String semester;
    private String batch;
    private String username;
    private String profileImagePath;
}