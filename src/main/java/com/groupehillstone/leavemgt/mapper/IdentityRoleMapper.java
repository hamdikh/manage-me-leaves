package com.groupehillstone.leavemgt.mapper;

import com.groupehillstone.leavemgt.entities.Collaborator;
import com.groupehillstone.leavemgt.identity.IdentityRole;
import org.mapstruct.Mapper;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring")
public class IdentityRoleMapper {

    public String toIdentityRole(Collaborator collaborator) {
        return ofNullable(collaborator).map(Collaborator::getIdentityRole).map(IdentityRole::name).orElse(null);
    }

    public Collaborator fromIdentityRole(String identityRole) {
        return ofNullable(identityRole).map(IdentityRole::valueOf).map(identityRoleEnum -> {
            final Collaborator collaborator = new Collaborator();
            collaborator.setIdentityRole(identityRoleEnum);
            return collaborator;
        }).orElse(null);
    }

    public IdentityRole convertToEnum(String identityRole) {
        return IdentityRole.valueOf(identityRole);
    }

}
