package com.groupehillstone.leavemgt.controllers;

import com.groupehillstone.leavemgt.dto.LeaveDTO;
import com.groupehillstone.leavemgt.entities.Leave;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.mapper.LeaveMapper;
import com.groupehillstone.leavemgt.services.LeaveService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.SuccessResponse;
import com.groupehillstone.validators.LeaveValidator;
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

import java.util.*;

@CrossOrigin("*")
@Slf4j
@RequestMapping("api/leaves")
@RestController
public class LeaveController {

    private final Logger logger = LoggerFactory.getLogger(LeaveController.class);

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private LeaveMapper leaveMapper;

    @Autowired
    private LeaveValidator leaveValidator;

    @GetMapping("/")
    public ResponseEntity getAll() {
        ResponseEntity response;
        try {
            final List<LeaveDTO> leaves = leaveMapper.toDto(leaveService.findAll());
            response = ResponseEntity.status(HttpStatus.OK).body(leaves);
        } catch (final Exception e) {
            logger.error("Error retrieving leaves list"+e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity getAllPageable(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        ResponseEntity responseEntity;
        try {
            List<LeaveDTO> leaves;
            Pageable paging = PageRequest.of(page, size);
            Page<Leave> pageLeaves = leaveService.findAll(paging);
            leaves = leaveMapper.toDto(pageLeaves.getContent());
            Map<String, Object> response = new HashMap<>();
            response.put("leaves", leaves);
            response.put("currentPage", pageLeaves.getNumber());
            response.put("totalItems", pageLeaves.getTotalElements());
            response.put("totalPages", pageLeaves.getTotalPages());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (final Exception e) {
            logger.error("Error retrieving pageable leaves list", e);
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return responseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final LeaveDTO leave = leaveMapper.toDto(leaveService.findById(id));
            response = ResponseEntity.status(HttpStatus.OK).body(leave);
        } catch (final Exception e) {
            logger.error("Error retrieving leave by id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/leave-request/{id}")
    public ResponseEntity getAllByLeaveRequestId(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final List<LeaveDTO> leaves = leaveMapper.toDto(leaveService.findAllByLeaveRequestId(id));
            response = ResponseEntity.status(HttpStatus.OK).body(leaves);
        } catch (final Exception e) {
            logger.error("Error retrieving leaves list by leave request id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody LeaveDTO leaveDTO) {
        ResponseEntity response;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveValidator.validate(leaveDTO);
            if(errorResponse == null) {
                final Leave leave = leaveService.create(leaveMapper.toEntity(leaveDTO));
                response = ResponseEntity.status(HttpStatus.OK).body(leaveMapper.toDto(leave));
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
        } catch (final Exception e) {
            logger.error("Error creating leave ",e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PostMapping("/all")
    public ResponseEntity createAll(@RequestBody List<LeaveDTO> leaves) {
        ResponseEntity response;
        ErrorResponse errorResponse = null;
        List<ErrorResponse> errors = new ArrayList<>();
        try {
            for(LeaveDTO leaveDTO : leaves) {
                errorResponse = leaveValidator.validate(leaveDTO);
                if(errorResponse != null) {
                    errors.add(errorResponse);
                }
            }
            if(errors.size() == 0) {
                final List<Leave> leaveList = leaveService.createAll(leaveMapper.toEntity(leaves));
                response = ResponseEntity.status(HttpStatus.OK).body(leaveList);
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
            }
        } catch (final Exception e) {
            logger.error("Error creating all leaves list ",e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") UUID id, @RequestBody LeaveDTO leaveDTO) {
        ResponseEntity response;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveValidator.validate(leaveDTO);
            if(errorResponse == null) {
                final Leave leave = leaveService.update(leaveMapper.toEntity(leaveDTO));
                response = ResponseEntity.status(HttpStatus.OK).body(leaveMapper.toDto(leave));
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
        } catch (final Exception e) {
            logger.error("Error updating leave with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            leaveService.delete(id);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("DELETED"));
        } catch (final Exception e) {
            logger.error("Error deleting leave with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

}
