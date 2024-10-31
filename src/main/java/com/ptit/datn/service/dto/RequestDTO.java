package com.ptit.datn.service.dto;

import com.ptit.datn.domain.Request;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class RequestDTO {
    public Long id;

    public Long userId;
    public UserDTO userDTO;

    public Set<Long> officeIds;
    public Set<OfficeDTO> officeDTOs;

    public LocalDate date;
    public LocalTime time;

    public String note;

    public Integer status;

    public RequestDTO() {
    }

    public RequestDTO(Request request) {
        this.id = request.getId();
        this.userId = request.getUserId();
        this.date = request.getDate();
        this.time = request.getTime();
        this.note = request.getNote();
        this.status = request.getStatus();
    }
}
