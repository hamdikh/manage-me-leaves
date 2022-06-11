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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
                if(LeaveStatus.VALIDATED.toString().equals(responseDTO.getStatus())) {
                    oldLeaveRequest.setStatus(LeaveStatus.PARTIALLY_VALIDATED);
                } else {
                    oldLeaveRequest.setStatus(LeaveStatus.valueOf(responseDTO.getStatus()));
                }
            } else if(oldLeaveRequest.getFirstValidator() != null && oldLeaveRequest.getSecondValidator() == null) {
                oldLeaveRequest.setSecondValidator(collaboratorMapper.toEntity(responseDTO.getValidator()));
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
    public List<LeaveRequest> searchWithCriteria(String status, String type, LocalDate createdAt) {
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder init = new StringBuilder("SELECT DISTINCT(l.*) FROM public.leave_requests as l");
        StringBuilder inner = new StringBuilder("");
        StringBuilder condition = new StringBuilder(" WHERE l.is_deleted = 'false' AND l.status <> 'DRAFT'");

        boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
        boolean typeCheck = StringUtils.isEmpty(type) && StringUtils.isBlank(type);
        boolean createdAtCheck = createdAt == null;

        if(!statusCheck) {
            condition.append(" AND l.status = '"+status+"'");
        }
        if(!createdAtCheck) {
            condition.append(" AND l.createdAt >= '"+createdAt+"'");
        }
        if(!typeCheck) {
            inner.append("INNER JOIN public.leave_requests_leaves AS lrl ON lrl.leave_request_id = l.id " +
                    "INNER JOIN public.leaves AS lv ON lv.id = lrl.leaves_id");
            condition.append(" AND lv.type = '"+type+"'");
        }

        queryBuilder.append(init).append(inner).append(condition);

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), LeaveRequest.class);
        List<LeaveRequest> leaveRequests = query.getResultList();
        return leaveRequests;

    }

    @Override
    public List<LeaveRequest> searchWithCriteriaForCollaborator(UUID collaboratorId, String status, String type, LocalDate createdAt) {
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder init = new StringBuilder("SELECT DISTINCT(l.*) FROM public.leave_requests as l");
        StringBuilder inner = new StringBuilder("");
        StringBuilder condition = new StringBuilder(" WHERE l.is_deleted = 'false'");

        boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
        boolean typeCheck = StringUtils.isEmpty(type) && StringUtils.isBlank(type);
        boolean createdAtCheck = createdAt == null;

        if(collaboratorId != null) {
            condition.append(" AND l.collaborator_id = '"+collaboratorId+"'");
        }
        if(!statusCheck) {
            condition.append(" AND l.status = '"+status+"'");
        }
        if(!createdAtCheck) {
            condition.append(" AND l.created_at >= '"+createdAt+"'");
        }
        if(!typeCheck) {
            inner.append(" INNER JOIN public.leave_requests_leaves AS lrl ON lrl.leave_request_id = l.id " +
                    "INNER JOIN public.leaves AS lv ON lv.id = lrl.leaves_id");
            condition.append(" AND lv.type = '"+type+"'");
        }

        queryBuilder.append(init).append(inner).append(condition);

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), LeaveRequest.class);
        List<LeaveRequest> leaveRequests = query.getResultList();
        return leaveRequests;
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
}
