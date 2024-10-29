package com.ptit.datn.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "district")
@Getter
public class District {
    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "province_id", referencedColumnName = "id")
    private Province province;
}
