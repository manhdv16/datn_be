package com.ptit.datn.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.Set;

@Entity
@Table(name = "building")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Building extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ward_id", referencedColumnName = "id")
    private Ward ward;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "number_of_floor", nullable = false)
    private Integer numberOfFloor;

    @Column(name = "number_of_basement", nullable = false)
    private Integer numberOfBasement;

    @Column(name = "price_per_m2", nullable = false)
    private BigInteger pricePerM2;

    @Column(name = "floor_height", nullable = false)
    private Double floorHeight;

    @Column(name = "floor_area", nullable = false)
    private Double floorArea;

    @Column(name = "facilities")
    private String facilities;

    @Column(name = "note")
    private String note;

    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Office> offices;
}
