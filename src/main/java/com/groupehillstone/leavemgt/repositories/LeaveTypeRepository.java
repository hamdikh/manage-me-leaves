package com.groupehillstone.leavemgt.repositories;

import com.groupehillstone.leavemgt.entities.LeaveType;
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
public interface LeaveTypeRepository extends JpaRepository<LeaveType, UUID> {

    boolean existsByName(String name);

    @Query("SELECT l FROM LeaveType l WHERE l.isDeleted = false AND l.id = :id")
    LeaveType findLeaveTypeById(UUID id);

    @Query("SELECT l FROM LeaveType l WHERE l.isDeleted = false")
    Page<LeaveType> findAll(Pageable pageable);

    @Query("SELECT l FROM LeaveType l WHERE l.isDeleted = false")
    List<LeaveType> findAll();

    @Transactional
    @Modifying
    @Query("UPDATE LeaveType l SET l.isDeleted = true WHERE l.id = :id")
    void delete(UUID id);

}
