package com.groupehillstone.leavemgt.services.impl;

import com.groupehillstone.leavemgt.entities.LeaveType;
import com.groupehillstone.leavemgt.repositories.LeaveTypeRepository;
import com.groupehillstone.leavemgt.services.LeaveTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Override
    public boolean existsByName(String name) {
        boolean response = false;
        try {
            response = leaveTypeRepository.existsByName(name);
        } catch (final Exception e) {
            logger.error("Error checking name : "+name, e);
        }
        return response;
    }

    @Override
    public LeaveType create(LeaveType leaveType) {
        try {
            leaveType = leaveTypeRepository.save(leaveType);
        } catch (final Exception e) {
            logger.error("Error creating type with name : "+leaveType.getName(), e);
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
}
