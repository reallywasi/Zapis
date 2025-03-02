package Zapis_Plateform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "faculty_attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String facultyUsername;

    @Column(nullable = false)
    private String classCode;

    @Column(nullable = false)
    private String roomNo;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String startTimeEndTime;

    @Column(nullable = false)
    private LocalDateTime markedAt;

    @Column(nullable = false)
    private boolean present;
}