package com.groupehillstone.leavemgt.services;

import com.groupehillstone.leavemgt.entities.Collaborator;

public interface CollaboratorService {

    Collaborator findByEmail(String email);
}
