package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveTypeDTO extends AbstractAuditableEntityDTO {

    private String wording;

    private String code;

}
