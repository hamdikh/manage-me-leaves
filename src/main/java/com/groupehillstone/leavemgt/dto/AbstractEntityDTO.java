package com.groupehillstone.leavemgt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractEntityDTO {

    protected String id;

    protected boolean isDeleted;

}