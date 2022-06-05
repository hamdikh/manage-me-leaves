package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestDTO extends AbstractAuditableEntityDTO {

    private String type;

    private String description;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private CollaboratorDTO collaborator;

    private CollaboratorDTO firstValidator;

    private CollaboratorDTO secondValidator;

}
