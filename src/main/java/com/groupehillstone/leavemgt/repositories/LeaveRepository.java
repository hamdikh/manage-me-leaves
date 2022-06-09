package com.groupehillstone.leavemgt.repositories;

import com.groupehillstone.leavemgt.entities.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, UUID> {

    @Transactional
    @Modifying
    @Query("UPDATE Leave l SET l.isDeleted = true WHERE l.id = :id")
    void delete(UUID id);

    @Query("SELECT l FROM Leave l WHERE l.isDeleted = false AND l.leaveRequest.id = :id")
    List<Leave> findAllByLeaveRequestId(UUID id);

    @Query("SELECT l FROM Leave l WHERE l.isDeleted = false AND l.id = :id")
    Leave findLeaveById(UUID id);

    @Query("SELECT l FROM Leave l WHERE l.isDeleted = false")
    List<Leave> findAll();

    @Query("SELECT l FROM Leave l WHERE l.isDeleted = false")
    Page<Leave> findAll(Pageable pageable);

}
