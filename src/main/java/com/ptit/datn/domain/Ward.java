package com.ptit.datn.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "ward", indexes = {
        @Index(name = "ward_name_index", columnList = "name")
})
@Getter
public class Ward {
    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private District district;
}
