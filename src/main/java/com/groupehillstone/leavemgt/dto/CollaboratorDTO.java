package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollaboratorDTO extends AbstractAuditableEntityDTO {

    private String firstName;

    private String lastName;

    private String email;

    private String identityRole;

    private BusinessUnitDTO businessUnit;

    private CollaboratorDTO salesManager;
}
