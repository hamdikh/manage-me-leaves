package com.groupehillstone.leavemgt.mapper;

import com.groupehillstone.leavemgt.dto.CollaboratorDTO;
import com.groupehillstone.leavemgt.entities.Collaborator;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UUIDMapper.class})
public interface CollaboratorMapper extends EntityMapper<CollaboratorDTO, Collaborator> {
}
