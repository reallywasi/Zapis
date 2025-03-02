package Zapis_Plateform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_time_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String course;

    @Column
    private String specialisation;

    @Column(nullable = false)
    private String batch;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String roomNo;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String startTimeEndTime;

    @Column(nullable = false)
    private String facultyName;  

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String semester;

    @ManyToOne
    @JoinColumn(name = "apo_user_id", nullable = false)
    private APO_Dashboard apoUser;
}