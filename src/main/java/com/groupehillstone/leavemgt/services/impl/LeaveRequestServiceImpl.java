package com.groupehillstone.leavemgt.services.impl;

import com.groupehillstone.leavemgt.dto.LeaveRequestDTO;
import com.groupehillstone.leavemgt.dto.ResponseDTO;
import com.groupehillstone.leavemgt.entities.Leave;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.mapper.CollaboratorMapper;
import com.groupehillstone.leavemgt.mapper.LeaveRequestMapper;
import com.groupehillstone.leavemgt.repositories.LeaveRequestRepository;
import com.groupehillstone.leavemgt.services.LeaveRequestService;
import com.querydsl.core.types.Predicate;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final Logger logger = LoggerFactory.getLogger(LeaveRequestServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private CollaboratorMapper collaboratorMapper;

    @Override
    public Page<LeaveRequest> findAll(Predicate predicate, Pageable pageable) {
        Page<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findAll(predicate, pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving predicatable pageable list for leave request", e);
        }
        return leaveRequests;
    }

    @Override
    public Page<LeaveRequest> findAll(Pageable pageable) {
        Page<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findAll(pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests pageable list ", e);
        }
        return leaveRequests;
    }

    @Override
    public List<LeaveRequest> findAll() {
        List<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findAll();
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests list ",e);
        }
        return leaveRequests;
    }

    @Override
    public LeaveRequest findById(UUID id) {
        LeaveRequest leaveRequest = null;
        try {
            leaveRequest = leaveRequestRepository.findLeaveRequestById(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leave request by id : "+id , e);
        }
        return leaveRequest;
    }

    @Override
    public Page<LeaveRequest> findLeaveRequestByCollaboratorId(UUID id, Pageable pageable) {
        Page<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findLeaveRequestByCollaboratorId(id, pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving leave request for collaborator with id : "+id, e);
        }
        return leaveRequests;
    }

    @Override
    public LeaveRequest create(LeaveRequest leaveRequest) {
        try {
            leaveRequest = leaveRequestRepository.save(leaveRequest);
        } catch (final Exception e) {
            logger.error("Error creating leave request for : "+leaveRequest.getCollaborator().getFirstName(), e);
        }
        return leaveRequest;
    }

    @Override
    public LeaveRequest update(LeaveRequest leaveRequest) {
        try {
            leaveRequest = leaveRequestRepository.save(leaveRequest);
        } catch (final Exception e) {
            logger.error("Error updating leave request for collaborator with id : "+leaveRequest.getId(), e);
        }
        return leaveRequest;
    }

    @Override
    public LeaveRequest response(UUID id, ResponseDTO responseDTO) {
        LeaveRequest leaveRequest = null;
        try {
            final LeaveRequest oldLeaveRequest = leaveRequestRepository.findLeaveRequestById(id);
            if(oldLeaveRequest.getFirstValidator() == null && oldLeaveRequest.getSecondValidator() == null) {
                oldLeaveRequest.setFirstValidator(collaboratorMapper.toEntity(responseDTO.getValidator()));
                oldLeaveRequest.setFirstValidationAt(responseDTO.getValidatedAt());
                if(LeaveStatus.VALIDATED.toString().equals(responseDTO.getStatus())) {
                    oldLeaveRequest.setStatus(LeaveStatus.PARTIALLY_VALIDATED);
                } else {
                    oldLeaveRequest.setStatus(LeaveStatus.valueOf(responseDTO.getStatus()));
                }
            } else if(oldLeaveRequest.getFirstValidator() != null && oldLeaveRequest.getSecondValidator() == null) {
                oldLeaveRequest.setSecondValidator(collaboratorMapper.toEntity(responseDTO.getValidator()));
                oldLeaveRequest.setSecondValidationAt(responseDTO.getValidatedAt());
                oldLeaveRequest.setStatus(LeaveStatus.valueOf(responseDTO.getStatus()));
            }
            leaveRequest = leaveRequestRepository.save(oldLeaveRequest);
        } catch (final Exception e) {
            logger.error("Error responding leave request with id : "+id, e);
        }
        return leaveRequest;
    }

    @Override
    public void delete(UUID id) {
        try {
            if(leaveRequestRepository.existsById(id)) {
                leaveRequestRepository.delete(id);
            }
        } catch (final Exception e) {
            logger.error("Error deleting leave request with id : "+id, e);
        }
    }

    @Override
    public Page<LeaveRequest> searchWithCriteria(String status, UUID typeId, LocalDate createdAt, UUID businessUnitId, Pageable paging) {
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder queryBuilderCount = new StringBuilder();
        StringBuilder init = new StringBuilder("SELECT DISTINCT(l.*) FROM public.leave_requests as l");
        StringBuilder count = new StringBuilder("SELECT DISTINCT(COUNT(l.id)) FROM public.leave_requests as l");
        StringBuilder inner = new StringBuilder("");
        StringBuilder condition = new StringBuilder(" WHERE l.is_deleted = 'false' AND l.status <> 'DRAFT'");

        boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
        boolean typeIdCheck = typeId == null;
        boolean createdAtCheck = createdAt == null;
        boolean businessUnitIdCheck = businessUnitId == null;

        if(!statusCheck) {
            condition.append(" AND l.status = '"+status+"'");
        }
        if(!createdAtCheck) {
            condition.append(" AND l.createdAt >= '"+createdAt+"'");
        }
        if(!typeIdCheck) {
            inner.append(" INNER JOIN public.leave_requests_leaves AS lrl ON lrl.leave_request_id = l.id " +
                    "INNER JOIN public.leaves AS lv ON lv.id = lrl.leaves_id");
            condition.append(" AND lv.type_id = '"+typeId+"'");
        }
        if(!businessUnitIdCheck) {
            inner.append(" INNER JOIN public.collaborators AS c ON c.id = l.collaborator_id");
            condition.append(" AND c.business_unit_id = '"+businessUnitId+"'");
        }

        queryBuilder.append(init).append(inner).append(condition);

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), LeaveRequest.class);
        query.setFirstResult(paging.getPageNumber() * paging.getPageSize());
        query.setMaxResults(paging.getPageSize());
        List<LeaveRequest> leaveRequests = query.getResultList();

        queryBuilderCount.append(count).append(condition);
        Query countQuery = entityManager.createNativeQuery(queryBuilderCount.toString());
        long countResult = Long.parseLong(countQuery.getSingleResult().toString());

        Page leaveRequestsPage = new PageImpl(leaveRequests, paging, countResult);

        return leaveRequestsPage;

    }

    @Override
    public Page<LeaveRequest> searchWithCriteriaForCollaborator(UUID collaboratorId, String status, UUID typeId, LocalDate createdAt, String keywords, Pageable paging) {
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder queryBuilderCount = new StringBuilder();
        StringBuilder init = new StringBuilder("SELECT DISTINCT(l.*) FROM public.leave_requests as l");
        StringBuilder count = new StringBuilder("SELECT DISTINCT(COUNT(l.id)) FROM public.leave_requests as l");
        StringBuilder inner = new StringBuilder("");
        StringBuilder condition = new StringBuilder(" WHERE l.is_deleted = 'false'");

        boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
        boolean typeIdCheck = typeId == null;
        boolean createdAtCheck = createdAt == null;
        boolean keywordsCheck = StringUtils.isNotEmpty(keywords) && StringUtils.isNotBlank(keywords);

        if(collaboratorId != null) {
            condition.append(" AND l.collaborator_id = '"+collaboratorId+"'");
        }
        if(!statusCheck) {
            condition.append(" AND l.status = '"+status+"'");
        }
        if(!createdAtCheck) {
            condition.append(" AND l.created_at >= '"+createdAt+"'");
        }
        if(!typeIdCheck) {
            inner.append(" INNER JOIN public.leave_requests_leaves AS lrl ON lrl.leave_request_id = l.id " +
                    "INNER JOIN public.leaves AS lv ON lv.id = lrl.leaves_id");
            condition.append(" AND lv.type_id = '"+typeId+"'");
        }
        if(keywordsCheck) {
            inner.append(" INNER JOIN public.collaborators AS c ON c.id = l.collaborator_id");
            condition.append(" AND (LOWER(c.first_name) LIKE '%"+keywords+"%' OR LOWER(c.last_name) LIKE '%"+keywords+"%')");
        }

        queryBuilder.append(init).append(inner).append(condition);

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), LeaveRequest.class);
        query.setFirstResult(paging.getPageNumber() * paging.getPageSize());
        query.setMaxResults(paging.getPageSize());
        List<LeaveRequest> leaveRequests = query.getResultList();

        queryBuilderCount.append(count).append(condition);
        Query countQuery = entityManager.createNativeQuery(queryBuilderCount.toString());
        long countResult = Long.parseLong(countQuery.getSingleResult().toString());

        Page leaveRequestsPage = new PageImpl(leaveRequests, paging, countResult);

        return leaveRequestsPage;
    }

    @Override
    public LeaveRequest findLeaveRequestByLeaveId(UUID id) {
        LeaveRequest leaveRequest = null;
        try {
            leaveRequest = leaveRequestRepository.findLeaveRequestByLeaveId(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leave request by leave id : "+id, e);
        }
        return leaveRequest;
    }

    @Override
    public Page<LeaveRequest> findLeaveRequestsBySalesManagerId(UUID id, Pageable pageable) {
        Page<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findLeaveRequestsBySalesManagerId(id, pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving pageable leave requests list for sales manager with id : "+id, e);
        }
        return leaveRequests;
    }

    @Override
    public Page<LeaveRequest> findLeaveRequestsByManagerId(UUID id, Pageable pageable) {
        Page<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findLeaveRequestsByManagerId(id, pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving pageable leave requests list for manager with id : "+id, e);
        }
        return leaveRequests;
    }

    @Override
    public Page<LeaveRequest> searchWithCriteriaForSales(UUID salesManagerId, String status, UUID typeId, LocalDate createdAt, UUID businessUnitId, String keywords, Pageable paging) {
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder queryBuilderCount = new StringBuilder();
        StringBuilder init = new StringBuilder("SELECT DISTINCT(l.*) FROM public.leave_requests AS l");
        StringBuilder count = new StringBuilder("SELECT DISTINCT(COUNT(l.id)) FROM public.leave_requests AS l");
        StringBuilder inner = new StringBuilder("");
        StringBuilder condition = new StringBuilder(" WHERE l.is_deleted = 'false' AND l.status <> 'DRAFT'");

        boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
        boolean typeIdCheck = typeId == null;
        boolean createdAtCheck = createdAt == null;
        boolean businessUnitIdCheck = businessUnitId == null;
        boolean keywordsCheck = StringUtils.isNotEmpty(keywords) && StringUtils.isNotBlank(keywords);

        if(salesManagerId != null || !businessUnitIdCheck || keywordsCheck) {
            inner.append(" INNER JOIN public.collaborators AS c ON c.id = l.collaborator_id");
            if(salesManagerId != null) {
                condition.append(" AND c.identity_role IN ('EMPLOYEE', 'TEAM_MANAGER') AND c.sales_manager_id = '"+salesManagerId+"'");
            }
            if(!businessUnitIdCheck) {
                condition.append(" AND c.business_unit_id = '"+businessUnitId+"'");
            }
            if(keywordsCheck) {
                condition.append(" AND (LOWER(c.first_name) LIKE '%"+keywords+"%' OR LOWER(c.last_name) LIKE '%"+keywords+"%')");
            }
        }
        if(!statusCheck) {
            condition.append(" AND l.status = '"+status+"'");
        }
        if(!createdAtCheck) {
            condition.append(" AND l.created_at >= '"+createdAt+"'");
        }
        if(!typeIdCheck) {
            inner.append(" INNER JOIN public.leave_requests_leaves AS lrl ON lrl.leave_request_id = l.id " +
                    "INNER JOIN public.leaves AS lv ON lv.id = lrl.leaves_id");
            condition.append(" AND lv.type_id = '"+typeId+"'");
        }

        queryBuilder.append(init).append(inner).append(condition);

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), LeaveRequest.class);
        query.setFirstResult(paging.getPageNumber() * paging.getPageSize());
        query.setMaxResults(paging.getPageSize());
        List<LeaveRequest> leaveRequests = query.getResultList();

        queryBuilderCount.append(count).append(condition);
        Query countQuery = entityManager.createNativeQuery(queryBuilderCount.toString());
        long countResult = Long.parseLong(countQuery.getSingleResult().toString());

        Page leaveRequestsPage = new PageImpl(leaveRequests, paging, countResult);

        return leaveRequestsPage;
    }

    @Override
    public Page<LeaveRequest> searchWithCriteriaForManager(UUID managerId, String status, UUID typeId, LocalDate createdAt, UUID businessUnitId, String keywords, Pageable paging) {
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder queryBuilderCount = new StringBuilder();
        StringBuilder init = new StringBuilder("SELECT DISTINCT(l.*) FROM public.leave_requests AS l");
        StringBuilder count = new StringBuilder("SELECT DISTINCT(COUNT(l.id)) FROM public.leave_requests AS l");
        StringBuilder inner = new StringBuilder("");
        StringBuilder condition = new StringBuilder(" WHERE l.is_deleted = 'false' AND l.status <> 'DRAFT'");

        boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
        boolean typeIdCheck = typeId == null;
        boolean createdAtCheck = createdAt == null;
        boolean businessUnitIdCheck = businessUnitId == null;

        if(managerId != null || !businessUnitIdCheck) {
            inner.append(" INNER JOIN public.collaborators AS c ON c.id = l.collaborator_id");
            if(managerId != null) {
                condition.append(" AND c.identity_role IN ('BUSINESS_UNIT_MANAGER', 'BUSINESS', 'RH', 'ADMIN') AND c.manager_id = '"+managerId+"'");
            }
            if(!businessUnitIdCheck) {
                condition.append(" AND c.business_unit_id = '"+businessUnitId+"'");
            }
        }
        if(!statusCheck) {
            condition.append(" AND l.status = '"+status+"'");
        }
        if(!createdAtCheck) {
            condition.append(" AND l.created_at >= '"+createdAt+"'");
        }
        if(!typeIdCheck) {
            inner.append(" INNER JOIN public.leave_requests_leaves AS lrl ON lrl.leave_request_id = l.id " +
                    "INNER JOIN public.leaves AS lv ON lv.id = lrl.leaves_id");
            condition.append(" AND lv.type_id = '"+typeId+"'");
        }
        if(StringUtils.isNotEmpty(keywords) && StringUtils.isNotBlank(keywords)) {
            inner.append(" INNER JOIN public.collaborators AS c ON c.id = l.collaborator_id");
            condition.append(" AND (LOWER(c.first_name) LIKE '%"+keywords+"%' OR LOWER(c.last_name) LIKE '%"+keywords+"%')");
        }

        queryBuilder.append(init).append(inner).append(condition);

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), LeaveRequest.class);
        query.setFirstResult(paging.getPageNumber() * paging.getPageSize());
        query.setMaxResults(paging.getPageSize());
        List<LeaveRequest> leaveRequests = query.getResultList();

        queryBuilderCount.append(count).append(condition);
        Query countQuery = entityManager.createNativeQuery(queryBuilderCount.toString());
        long countResult = Long.parseLong(countQuery.getSingleResult().toString());

        Page leaveRequestsPage = new PageImpl(leaveRequests, paging, countResult);

        return leaveRequestsPage;
    }

    @Override
    public List<LeaveRequest> findLeaveRequestsByCollaboratorId(UUID id) {
        List<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findLeaveRequestsByCollaboratorId(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests list by collaborator id : "+id, e);
        }
        return leaveRequests;
    }

    @Override
    public List<LeaveRequest> findLeaveRequestsByBusinessUnitId(UUID id) {
        List<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findLeaveRequestsByBusinessUnitId(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests list by collaborator id : "+id, e);
        }
        return leaveRequests;
    }

    @Override
    public List<LeaveRequest> findLeaveRequestsByBusinessUnitIdForBUM(UUID id) {
        List<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findLeaveRequestsByBusinessUnitIdForBUM(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests list by collaborator id : "+id, e);
        }
        return leaveRequests;
    }

    @Override
    public List<LeaveRequest> findLeaveRequestsByTeamId(UUID id) {
        List<LeaveRequest> leaveRequests = null;
        try {
            final List<LeaveRequest> leaveRequests1 = leaveRequestRepository.findLeaveRequestsByTeamId(id);
            final List<LeaveRequest> leaveRequests2 = leaveRequestRepository.findLeaveRequestsByTeamIdForTL(id);
            leaveRequests = ListUtils.union(leaveRequests1, leaveRequests2);
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests list by collaborator id : "+id, e);
        }
        return leaveRequests;
    }

    @Override
    public List<LeaveRequest> findLeaveRequestsForTeam(UUID id) {
        List<LeaveRequest> leaveRequests = null;
        try {
            leaveRequests = leaveRequestRepository.findLeaveRequestsByTeamId(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests list for team with id : "+id, e);
        }
        return leaveRequests;
    }
}
