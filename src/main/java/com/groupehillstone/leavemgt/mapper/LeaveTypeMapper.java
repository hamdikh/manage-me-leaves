package com.groupehillstone.leavemgt.mapper;

import com.groupehillstone.leavemgt.dto.LeaveTypeDTO;
import com.groupehillstone.leavemgt.entities.LeaveType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UUIDMapper.class})
public interface LeaveTypeMapper extends EntityMapper<LeaveTypeDTO, LeaveType> {
}
