package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HolidayDTO extends AbstractAuditableEntityDTO {

    private LocalDate date;

    private String designation;

    private Boolean isEnabled;

    private String zone;

    private int year;

}
