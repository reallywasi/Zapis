package Zapis_Plateform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_dashboard")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student_Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // College email as username

    @Column(nullable = false)
    private String data; // Dashboard-specific data

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentDetails student; // Link to StudentDetails
}