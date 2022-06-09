package com.groupehillstone.leavemgt.services;

import com.groupehillstone.leavemgt.entities.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LeaveService {

    Leave create(Leave leave);

    List<Leave> createAll(List<Leave> leaves);

    Leave update(Leave leave);

    void delete(UUID id);

    List<Leave> findAll();

    Page<Leave> findAll(Pageable pageable);

    Leave findById(UUID id);

    List<Leave> findAllByLeaveRequestId(UUID id);

}
