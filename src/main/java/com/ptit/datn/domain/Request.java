package com.ptit.datn.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "request")
@Getter
@Setter
public class Request extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "request_office",
        joinColumns = @JoinColumn(name = "request_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "office_id", nullable = false)
    )
    private Set<Office> offices = new HashSet<>();

    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "note")
    private String note;

    /***
     * Refer to src/main/java/com/ptit/datn/constants/RequestStatus.java
     */
    @Column(name = "status", nullable = false)
    private Integer status;
}
