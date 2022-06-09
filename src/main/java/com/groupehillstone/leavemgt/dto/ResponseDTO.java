package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResponseDTO extends AbstractAuditableEntityDTO {

    private String status;

    private List<LeaveDTO> leaves;

    private CollaboratorDTO collaborator;

    private CollaboratorDTO validator;

}
