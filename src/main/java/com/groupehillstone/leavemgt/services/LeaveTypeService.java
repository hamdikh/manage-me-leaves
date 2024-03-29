package com.groupehillstone.leavemgt.services;

import com.groupehillstone.leavemgt.entities.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LeaveTypeService {

    boolean existsByName(String name);

    boolean existsByCode(String code);

    LeaveType create(LeaveType leaveType);

    LeaveType update(LeaveType leaveType);

    void delete(UUID id);

    LeaveType findById(UUID id);

    List<LeaveType> findAll();

    Page<LeaveType> findAll(Pageable pageable);

    Page<LeaveType> searchWithCriteria(String keywords, Pageable pageable);

}
