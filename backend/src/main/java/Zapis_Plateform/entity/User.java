package Zapis_Plateform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String typeOfIndustry;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String organizationName;  // University/Bank name

    @Column(nullable = false, unique = true)
    private String organizationCode;

    private String logoPath; // Store the file path of the uploaded logo

    @Column(nullable = false)
    private Boolean approved = false; // False until approved by admin
}
