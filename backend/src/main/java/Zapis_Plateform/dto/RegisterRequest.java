package Zapis_Plateform.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterRequest {
    private String typeOfIndustry;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String organizationName;
    private String organizationCode;
    private MultipartFile logo; // File upload
}
