package Zapis_Plateform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "apo_dashboard")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class APO_Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // Linked to the user

    @Column(nullable = false)
    private String data; // Example field for APO data

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Reference to the User entity
}