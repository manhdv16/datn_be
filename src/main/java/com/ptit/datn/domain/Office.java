package com.ptit.datn.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

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
}
