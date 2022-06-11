package com.groupehillstone.leavemgt.repositories;

import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID>, QuerydslPredicateExecutor<LeaveRequest> {

    Page<LeaveRequest> findAll(Predicate predicate, Pageable pageable);

    @Query("SELECT l FROM LeaveRequest l WHERE l.isDeleted = false AND l.status <> 'DRAFT'")
    Page<LeaveRequest> findAll(Pageable pageable);

    @Query("SELECT l FROM LeaveRequest l WHERE l.isDeleted = false")
    List<LeaveRequest> findAll();

    @Query("SELECT l FROM LeaveRequest l WHERE l.isDeleted = false AND l.id = :id")
    LeaveRequest findLeaveRequestById(UUID id);

    @Query("SELECT l FROM LeaveRequest l WHERE l.isDeleted = false AND l.collaborator.id = :id")
    Page<LeaveRequest> findLeaveRequestByCollaboratorId(UUID id, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE LeaveRequest l SET l.isDeleted = true WHERE l.id = :id")
    void delete(UUID id);

    @Query(value = "SELECT * FROM public.leave_requests AS l INNER JOIN public.leave_requests_leaves AS ll ON ll.leave_request_id = l.id WHERE ll.leaves_id = :id", nativeQuery = true)
    LeaveRequest findLeaveRequestByLeaveId(UUID id);

    @Query(value = "SELECT l FROM LeaveRequest l WHERE l.isDeleted = false AND l.collaborator.salesManager.id = :id")
    Page<LeaveRequest> findLeaveRequestsBySalesManagerId(UUID id, Pageable pageable);

}
