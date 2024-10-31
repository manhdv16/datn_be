package com.ptit.datn.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "office")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office extends AbstractAuditingEntity<Long> {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "area")
        private Double area;

        @Column(name = "floor")
        private Integer floor;

        @Column(name = "price")
        private BigInteger price;

        @Column(name = "note")
        private String note;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "building_id", nullable = false, referencedColumnName = "id", updatable = false)
        private Building building;

        @Column(name = "status", nullable = false)
        private Integer status; // 0: available, 1: rented, 2: not available

        @ManyToMany(mappedBy = "offices", fetch = FetchType.LAZY)
        private Set<Request> requests = new HashSet<>();

//        @ManyToMany(fetch = FetchType.LAZY)
//        @JoinTable(
//                name = "office_image",
//                joinColumns = @JoinColumn(name = "office_id"),
//                inverseJoinColumns = @JoinColumn(name = "image_id")
//        )
//        private Set<Image> images;
}
