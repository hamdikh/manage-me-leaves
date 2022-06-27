package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AbstractAuditableEntityDTO extends AbstractEntityDTO {

    protected LocalDateTime createdAt;

    protected CollaboratorDTO createdBy;

    protected LocalDateTime updatedAt;

    protected CollaboratorDTO updatedBy;

}