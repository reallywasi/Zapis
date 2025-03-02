package Zapis_Plateform.dto;

import lombok.Data;

@Data
public class FacultyDetailsDTO {
    private Long id;
    private String name;
    private String sap;
    private String registrationId;
    private String collegeEmail;
    private String department;
    private String username;
    private String profileImagePath;
}