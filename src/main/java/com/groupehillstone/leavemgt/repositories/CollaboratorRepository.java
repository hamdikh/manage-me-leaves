package com.groupehillstone.leavemgt.repositories;

import com.groupehillstone.leavemgt.entities.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, UUID> {

    @Query("SELECT c FROM Collaborator c WHERE c.isDeleted = false AND c.email = :email")
    Collaborator findByEmail(String email);

    @Query("SELECT c FROM Collaborator c WHERE c.isDeleted = false AND c.id = :id")
    Collaborator findCollaboratorById(UUID id);

}
