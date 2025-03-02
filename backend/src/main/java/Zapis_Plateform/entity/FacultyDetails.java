package Zapis_Plateform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faculty_details", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"collegeEmail"}),
    @UniqueConstraint(columnNames = {"sap"}),
    @UniqueConstraint(columnNames = {"registrationId", "username"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sap;

    @Column(nullable = false)
    private String registrationId;

    @Column(nullable = false, unique = true)
    private String collegeEmail;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String username;

    @Column
    private String profileImagePath; // New field for profile image

    @ManyToOne
    @JoinColumn(name = "apo_user_id", nullable = false)
    private APO_Dashboard apoUser;
}