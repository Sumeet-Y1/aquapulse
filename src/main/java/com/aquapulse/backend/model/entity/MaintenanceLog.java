package com.aquapulse.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate maintenanceDate;

    @Column(nullable = false)
    private String type; // e.g., "Filter Cleaning", "Inspection"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String notes;

    private LocalDate nextDueDate;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private RWHUnit unit;

    public enum Status {
        PENDING, COMPLETED, OVERDUE
    }
}