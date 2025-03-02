package Zapis_Plateform.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}