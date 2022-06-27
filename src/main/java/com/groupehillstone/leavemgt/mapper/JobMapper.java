package com.groupehillstone.leavemgt.mapper;

import com.groupehillstone.leavemgt.dto.JobDTO;
import com.groupehillstone.leavemgt.entities.Job;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UUIDMapper.class, IdentityRoleMapper.class})
public interface JobMapper extends EntityMapper<JobDTO, Job> {
}

