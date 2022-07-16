package com.groupehillstone.leavemgt.services.impl;

import com.groupehillstone.leavemgt.entities.LeaveType;
import com.groupehillstone.leavemgt.repositories.LeaveTypeRepository;
import com.groupehillstone.leavemgt.services.LeaveTypeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Override
    public boolean existsByName(String name) {
        boolean response = false;
        try {
            response = leaveTypeRepository.existsByWording(name);
        } catch (final Exception e) {
            logger.error("Error checking name : "+name, e);
        }
        return response;
    }

    @Override
    public boolean existsByCode(String code) {
        boolean response = false;
        try {
            response = leaveTypeRepository.existsByCode(code);
        } catch (final Exception e) {
            logger.error("Error checking name : "+code, e);
        }
        return response;
    }

    @Override
    public LeaveType create(LeaveType leaveType) {
        try {
            leaveType = leaveTypeRepository.save(leaveType);
        } catch (final Exception e) {
            logger.error("Error creating type with name : "+leaveType.getWording(), e);
        }
        return leaveType;
    }

    @Override
    public LeaveType update(LeaveType leaveType) {
        try {
            leaveType = leaveTypeRepository.save(leaveType);
        } catch (final Exception e) {
            logger.error("Error updating type with id : "+leaveType.getId(), e);
        }
        return leaveType;
    }

    @Override
    public void delete(UUID id) {
        try {
            if(leaveTypeRepository.existsById(id)) {
                leaveTypeRepository.delete(id);
            }
        } catch (final Exception e) {
            logger.error("Error deleting type with id : "+id, e);
        }
    }

    @Override
    public LeaveType findById(UUID id) {
        LeaveType leaveType = null;
        try {
            leaveType = leaveTypeRepository.findLeaveTypeById(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leave type with id : "+id, e);
        }
        return leaveType;
    }

    @Override
    public List<LeaveType> findAll() {
        List<LeaveType> leaveTypes = null;
        try {
            leaveTypes = leaveTypeRepository.findAll();
        } catch (final Exception e) {
            logger.error("Error retrieving leave types list ",e);
        }
        return leaveTypes;
    }

    @Override
    public Page<LeaveType> findAll(Pageable pageable) {
        Page<LeaveType> leaveTypes = null;
        try {
            leaveTypes = leaveTypeRepository.findAll(pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving pageable leave types list ",e);
        }
        return leaveTypes;
    }

    @Override
    public Page<LeaveType> searchWithCriteria(String keywords, Pageable pageable) {
        StringBuilder queryBuilder = new StringBuilder("");
        StringBuilder queryBuilderCount = new StringBuilder("");
        StringBuilder init = new StringBuilder("SELECT DISTINCT(l.*) FROM public.leave_types AS l");
        StringBuilder count = new StringBuilder("SELECT DISTINCT(COUNT(l.id)) FROM public.leave_types AS l");
        StringBuilder condition = new StringBuilder(" WHERE l.is_deleted = 'false'");
        StringBuilder order = new StringBuilder(" ORDER BY l.wording ASC");

        if(StringUtils.isNotBlank(keywords) && StringUtils.isNotBlank(keywords)) {
            condition.append(" AND LOWER(l.wording) LIKE '%"+keywords+"%' OR LOWER(l.code) LIKE '%"+keywords+"%'");
        }

        queryBuilder.append(init).append(condition).append(order);

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), LeaveType.class);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        final List<LeaveType> leaveTypes = query.getResultList();

        queryBuilderCount.append(count).append(condition);
        Query countQuery = entityManager.createNativeQuery(queryBuilderCount.toString());
        long countResult = Long.parseLong(countQuery.getSingleResult().toString());

        Page leaveTypePage = new PageImpl(leaveTypes, pageable, countResult);

        return leaveTypePage;
    }
}
