package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessUnitDTO extends AbstractAuditableEntityDTO {

    private String code;

    private String name;
}
