package com.ptit.datn.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "province")
@Getter
public class Province {
    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
