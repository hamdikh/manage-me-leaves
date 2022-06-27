package com.groupehillstone.leavemgt.services.impl;

import com.groupehillstone.leavemgt.entities.Collaborator;
import com.groupehillstone.leavemgt.repositories.CollaboratorRepository;
import com.groupehillstone.leavemgt.services.CollaboratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollaboratorServiceImpl implements CollaboratorService {

    private final Logger logger = LoggerFactory.getLogger(CollaboratorServiceImpl.class);

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Override
    public Collaborator findByEmail(String email) {
        Collaborator collaborator = null;
        try {
            collaborator = collaboratorRepository.findByEmail(email);
        } catch (final Exception e) {
            logger.error("Error retrieving collaborator with email : "+email, e);
        }
        return collaborator;
    }

    @Override
    public Collaborator findCollaboratorById(UUID id) {
        Collaborator collaborator = null;
        try {
            collaborator = collaboratorRepository.findCollaboratorById(id);
        } catch (final Exception e) {
            logger.error("Error retrieving collaborator with id : "+id, e);
        }
        return collaborator;
    }
}
