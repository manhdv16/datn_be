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

        @Column(name = "rental_price")
        private BigInteger rentalPrice;

        @Column(name = "building_id")
        private Long buildingId;

        @Column(name = "note")
        private String note;

}
