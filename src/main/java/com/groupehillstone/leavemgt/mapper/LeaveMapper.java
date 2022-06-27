package com.groupehillstone.leavemgt.mapper;

import com.groupehillstone.leavemgt.dto.LeaveDTO;
import com.groupehillstone.leavemgt.entities.Leave;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UUIDMapper.class})
public interface LeaveMapper extends EntityMapper<LeaveDTO, Leave> {
}
