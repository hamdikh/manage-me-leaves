package com.groupehillstone.leavemgt.mapper;

import org.mapstruct.Mapper;

import java.util.UUID;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring")
public class UUIDMapper {

    public String uuidToString(UUID id) {
        return ofNullable(id)
                .map(UUID::toString)
                .orElse(null);
    }

    public UUID stringToUUID(String idStr) {
        return ofNullable(idStr)
                .map(UUID::fromString)
                .orElse(null);
    }
}
