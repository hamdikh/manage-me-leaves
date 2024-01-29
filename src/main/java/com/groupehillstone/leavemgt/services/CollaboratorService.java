package com.groupehillstone.leavemgt.services;

import com.groupehillstone.leavemgt.entities.Collaborator;

import java.util.List;
import java.util.UUID;

public interface CollaboratorService {

    Collaborator findByEmail(String email);

    Collaborator findCollaboratorById(UUID id);

    List<String> findRHEmail();
}
