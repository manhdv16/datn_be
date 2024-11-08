package com.ptit.datn.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class RequestFormDTO {
    private List<Long> offices;
    private Long tenantId;
}
