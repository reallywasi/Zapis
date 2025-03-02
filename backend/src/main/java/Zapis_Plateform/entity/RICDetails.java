package Zapis_Plateform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ric_details", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"roomNo", "username"}), // Room No. is unique for the same user
    @UniqueConstraint(columnNames = {"ipAddress", "username"}), // IP Address is unique for the same user
    @UniqueConstraint(columnNames = {"classCode", "username"}) // Class Code is unique for the same user
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RICDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNo;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String classCode;

    @Column(nullable = false)
    private String username;

    @ManyToOne
    @JoinColumn(name = "apo_user_id", nullable = false)
    private APO_Dashboard apoUser;
}