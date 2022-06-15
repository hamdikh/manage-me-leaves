package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LeaveDTO extends AbstractAuditableEntityDTO {

    private LeaveTypeDTO type;

    private String description;

    @ElementCollection
    private List<LocalDate> leaveDays = new ArrayList<>();

    private LeaveRequestDTO leaveRequest;
}
