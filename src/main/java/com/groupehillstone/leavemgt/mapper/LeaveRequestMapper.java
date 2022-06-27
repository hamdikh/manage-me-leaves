package com.groupehillstone.leavemgt.mapper;

import com.groupehillstone.leavemgt.dto.LeaveRequestDTO;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UUIDMapper.class})
public interface LeaveRequestMapper extends EntityMapper<LeaveRequestDTO, LeaveRequest>{
}
