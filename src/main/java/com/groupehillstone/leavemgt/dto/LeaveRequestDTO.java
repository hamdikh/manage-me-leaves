package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class LeaveRequestDTO extends AbstractAuditableEntityDTO {

    private String status;

    private List<LeaveDTO> leaves;

    private CollaboratorDTO collaborator;

    private CollaboratorDTO firstValidator;

    private CollaboratorDTO secondValidator;

    private LocalDateTime firstValidationAt;

    private LocalDateTime secondValidationAt;

}
