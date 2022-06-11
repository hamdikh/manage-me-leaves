package com.groupehillstone.leavemgt.services.impl;

import com.groupehillstone.leavemgt.entities.Leave;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.repositories.LeaveRepository;
import com.groupehillstone.leavemgt.services.LeaveRequestService;
import com.groupehillstone.leavemgt.services.LeaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Override
    public Leave create(Leave leave) {
        try {
            leave = leaveRepository.save(leave);
        } catch (final Exception e) {
            logger.error("Error creating leave "+e);
        }
        return leave;
    }

    @Override
    public List<Leave> createAll(List<Leave> leaves) {
        try {
            leaves = leaveRepository.saveAll(leaves);
        } catch (final Exception e) {
            logger.error("Error creating all leaves list ",e);
        }
        return leaves;
    }

    @Override
    public void addNew(Leave leave, UUID id) {
        try {
            leave = leaveRepository.save(leave);
            LeaveRequest leaveRequest = leaveRequestService.findById(id);
            leaveRequest.getLeaves().add(leave);
            leaveRequestService.update(leaveRequest);
        } catch (final Exception e) {
            logger.error("Error creating leave and adding it to leave request leaves list", e);
        }
    }

    @Override
    public Leave update(Leave leave) {
        try {
            leave = leaveRepository.save(leave);
        } catch (final Exception e) {
            logger.error("Error updating leave "+e);
        }
        return leave;
    }

    @Override
    public void delete(UUID id) {
        try {
            if(leaveRepository.existsById(id)) {
                LeaveRequest leaveRequest = leaveRequestService.findLeaveRequestByLeaveId(id);
                Leave leave = leaveRepository.findLeaveById(id);
                leaveRequest.getLeaves().remove(leave);
                leaveRepository.deleteById(id);
                leaveRequestService.update(leaveRequest);
            }
        } catch (final Exception e) {
            logger.error("Error deleting leave with id : "+id, e);
        }
    }

    @Override
    public List<Leave> findAll() {
        List<Leave> leaves = null;
        try {
            leaves = leaveRepository.findAll();
        } catch (final Exception e) {
            logger.error("Error retrieving leaves list"+e);
        }
        return leaves;
    }

    @Override
    public Page<Leave> findAll(Pageable pageable) {
        Page<Leave> leaves = null;
        try {
            leaves = leaveRepository.findAll(pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving leaves pageable list"+e);
        }
        return leaves;
    }

    @Override
    public Leave findById(UUID id) {
        Leave leave = null;
        try {
            leave = leaveRepository.findLeaveById(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leave with id : "+id, e);
        }
        return leave;
    }

    @Override
    public List<Leave> findAllByLeaveRequestId(UUID id) {
        List<Leave> leaves = null;
        try {
            leaves = leaveRepository.findAllByLeaveRequestId(id);
        } catch (final Exception e) {
            logger.error("Error retrieving leaves list for Leave Request with id : "+id, e);
        }
        return leaves;
    }
}
