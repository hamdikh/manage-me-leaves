package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LeaveDTO extends AbstractAuditableEntityDTO {

    private LeaveTypeDTO type;

    private String description;

    @ElementCollection
    private Map<LocalDate, String> leaveDays = new HashMap<>();

    private LeaveRequestDTO leaveRequest;
}
