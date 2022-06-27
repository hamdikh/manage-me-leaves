package com.groupehillstone.leavemgt.mapper;

import com.groupehillstone.leavemgt.dto.HolidayDTO;
import com.groupehillstone.leavemgt.entities.Holiday;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UUIDMapper.class})
public interface HolidayMapper extends EntityMapper<HolidayDTO, Holiday> {
}
