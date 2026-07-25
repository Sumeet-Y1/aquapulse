package com.aquapulse.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "rwh_units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RWHUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double tankCapacityLiters;

    @Column(nullable = false)
    private Double rooftopAreaSqm;

    private LocalDate installDate;

    @ManyToOne
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<WaterReading> readings;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<MaintenanceLog> maintenanceLogs;
}