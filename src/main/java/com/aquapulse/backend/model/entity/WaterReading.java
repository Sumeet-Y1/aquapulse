package com.aquapulse.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "water_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate readingDate;

    @Column(nullable = false)
    private Double waterCollectedLiters;

    @Column(nullable = false)
    private Double storageLevelPercent;

    private Double rainfallMm;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private RWHUnit unit;
}