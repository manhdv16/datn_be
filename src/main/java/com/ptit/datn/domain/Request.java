package com.ptit.datn.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Column(name = "office_id", nullable = false)
    private Long officeId;

//    @Column(name = "status", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private RequestStatus status;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "note")
    private String note;
}
