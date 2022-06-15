package com.groupehillstone.leavemgt.repositories;

import com.groupehillstone.leavemgt.entities.Leave;
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

    @Query("SELECT l FROM LeaveRequest l WHERE l.isDeleted = false AND l.status <> 'DRAFT' ORDER BY l.createdAt DESC")
    Page<LeaveRequest> findAll(Pageable pageable);

    @Query("SELECT l FROM LeaveRequest l WHERE l.isDeleted = false  ORDER BY l.createdAt DESC")
    List<LeaveRequest> findAll();

    @Query("SELECT l FROM LeaveRequest l WHERE l.isDeleted = false AND l.id = :id")
    LeaveRequest findLeaveRequestById(UUID id);

    @Query("SELECT l FROM LeaveRequest l WHERE l.isDeleted = false AND l.collaborator.id = :id  ORDER BY l.createdAt DESC")
    Page<LeaveRequest> findLeaveRequestByCollaboratorId(UUID id, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE LeaveRequest l SET l.isDeleted = true WHERE l.id = :id")
    void delete(UUID id);

    @Query(value = "SELECT * FROM public.leave_requests AS l INNER JOIN public.leave_requests_leaves AS ll ON ll.leave_request_id = l.id WHERE ll.leaves_id = :id", nativeQuery = true)
    LeaveRequest findLeaveRequestByLeaveId(UUID id);

    @Query(value = "SELECT l FROM LeaveRequest l WHERE l.isDeleted = false AND l.status <> 'DRAFT' AND l.collaborator.salesManager.id = :id  ORDER BY l.createdAt DESC")
    Page<LeaveRequest> findLeaveRequestsBySalesManagerId(UUID id, Pageable pageable);

    @Query(value = "SELECT l.* FROM public.leave_requests AS l INNER JOIN public.collaborators AS c ON c.id = l.collaborator_id WHERE l.is_deleted = 'false' AND l.status = 'VALIDATED' AND c.business_unit_id = :id  ORDER BY l.created_at DESC", nativeQuery = true)
    List<LeaveRequest> findLeaveRequestsByBusinessUnitId(UUID id);

    @Query(value = "SELECT l.* FROM public.leave_requests AS l INNER JOIN public.business_units AS bu ON bu.business_unit_leader_id = l.collaborator_id WHERE l.is_deleted = 'false' AND AND l.status = 'VALIDATED' bu.id = :id ORDER BY l.created_at DESC", nativeQuery = true)
    List<LeaveRequest> findLeaveRequestsByBusinessUnitIdForBUM(UUID id);

    @Query(value = "SELECT l.* FROM public.leave_requests AS l INNER JOIN public.collaborators AS c ON c.id = l.collaborator_id WHERE l.is_deleted = 'false' AND l.status = 'VALIDATED' AND c.team_id = :id ORDER BY l.created_at DESC", nativeQuery = true)
    List<LeaveRequest> findLeaveRequestsByTeamId(UUID id);

    @Query(value = "SELECT l.* FROM public.leave_requests AS l INNER JOIN public.teams AS t ON t.team_leader_id = l.collaborator_id WHERE l.is_deleted = 'false' AND l.status = 'VALIDATED' AND t.id = :id ORDER BY l.created_at DESC", nativeQuery = true)
    List<LeaveRequest> findLeaveRequestsByTeamIdForTL(UUID id);

    @Query(value = "SELECT l.* FROM public.leave_requests AS l WHERE l.is_deleted = 'false' AND l.status = 'VALIDATED' AND l.collaborator_id = :id ORDER BY l.created_at DESC", nativeQuery = true)
    List<LeaveRequest> findLeaveRequestsByCollaboratorId(UUID id);

}
