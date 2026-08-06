package com.aquapulse.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "societies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Society {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;

    @OneToMany(mappedBy = "society", cascade = CascadeType.ALL)
    private List<RWHUnit> units;

    @ManyToMany(mappedBy = "societies")
    private Set<User> members;
}