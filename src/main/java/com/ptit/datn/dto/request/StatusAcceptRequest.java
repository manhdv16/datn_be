package com.ptit.datn.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class StatusAcceptRequest {
    private LocalDate date;
    private LocalTime time;
}
