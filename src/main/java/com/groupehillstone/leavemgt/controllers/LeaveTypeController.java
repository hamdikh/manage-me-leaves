package com.groupehillstone.leavemgt.controllers;

import com.groupehillstone.leavemgt.dto.LeaveTypeDTO;
import com.groupehillstone.leavemgt.entities.LeaveType;
import com.groupehillstone.leavemgt.mapper.LeaveTypeMapper;
import com.groupehillstone.leavemgt.services.LeaveTypeService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.SuccessResponse;
import com.groupehillstone.validators.LeaveTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin("*")
@Slf4j
@RequestMapping("/api/leave-types")
@RestController
public class LeaveTypeController {

    private final Logger logger = LoggerFactory.getLogger(LeaveTypeController.class);

    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private LeaveTypeMapper leaveTypeMapper;

    @Autowired
    private LeaveTypeValidator leaveTypeValidator;

    @GetMapping("/")
    public ResponseEntity getAll() {
        ResponseEntity response = null;
        try {
            final List<LeaveTypeDTO> leaveTypes = leaveTypeMapper.toDto(leaveTypeService.findAll());
            response = ResponseEntity.status(HttpStatus.OK).body(leaveTypes);
        } catch (final Exception e) {
            logger.error("Error retrieving leave types list "+e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity getAllPageable(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size) {
        ResponseEntity responseEntity;
        try {
            List<LeaveTypeDTO> leaveTypes;
            Pageable paging = PageRequest.of(page, size);

            Page<LeaveType> pageLeaveType = leaveTypeService.findAll(paging);

            leaveTypes = leaveTypeMapper.toDto(pageLeaveType.getContent());
            Map<String, Object> response = new HashMap<>();
            response.put("leaveTypes", leaveTypes);
            response.put("currentPage", pageLeaveType.getNumber());
            response.put("totalItems", pageLeaveType.getTotalElements());
            response.put("totalPages", pageLeaveType.getTotalPages());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (final Exception e) {
            logger.error("Error retrieving leave types list "+e);
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return responseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final LeaveTypeDTO leaveType = leaveTypeMapper.toDto(leaveTypeService.findById(id));
            response = ResponseEntity.status(HttpStatus.OK).body(leaveType);
        } catch (final Exception e) {
            logger.error("Error retrieving leave type with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody LeaveTypeDTO leaveTypeDTO) {
        ResponseEntity response;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveTypeValidator.validate(leaveTypeDTO);
            if(errorResponse == null) {
                final LeaveType leaveType = leaveTypeService.create(leaveTypeMapper.toEntity(leaveTypeDTO));
                response = ResponseEntity.status(HttpStatus.OK).body(leaveTypeMapper.toDto(leaveType));
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
        } catch (final Exception e) {
            logger.error("Error creating leave type with name : "+leaveTypeDTO.getName(), e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") UUID id, @RequestBody LeaveTypeDTO leaveTypeDTO) {
        ResponseEntity response;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveTypeValidator.validate(leaveTypeDTO);
            if(errorResponse == null) {
                final LeaveType leaveType = leaveTypeService.update(leaveTypeMapper.toEntity(leaveTypeDTO));
                response = ResponseEntity.status(HttpStatus.OK).body(leaveTypeMapper.toDto(leaveType));
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
        } catch (final Exception e) {
            logger.error("Error updating leave type with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            leaveTypeService.delete(id);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("DELETED"));
        } catch (final Exception e) {
            logger.error("Error deleting leave type with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

}
