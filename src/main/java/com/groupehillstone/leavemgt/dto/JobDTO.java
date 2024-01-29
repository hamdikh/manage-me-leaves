package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class JobDTO extends AbstractAuditableEntityDTO {
    private String name;
    private Set<CollaboratorDTO> collaborators;
    private Integer rank;
    private List<String> identityRole;
}
