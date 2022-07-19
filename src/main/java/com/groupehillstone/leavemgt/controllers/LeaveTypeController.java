package com.groupehillstone.leavemgt.controllers;

import com.groupehillstone.leavemgt.dto.LeaveTypeDTO;
import com.groupehillstone.leavemgt.entities.LeaveType;
import com.groupehillstone.leavemgt.mapper.LeaveTypeMapper;
import com.groupehillstone.leavemgt.services.LeaveTypeService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.SuccessResponse;
import com.groupehillstone.validators.LeaveTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
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
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
    public ResponseEntity getAllPageable(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size,
                                         @RequestParam(required = false) String keywords,
                                         @RequestParam(defaultValue = "wording,desc") String[] sort) {
        ResponseEntity responseEntity;
        try {
            List<Sort.Order> orders = getOrders(sort);
            List<LeaveTypeDTO> leaveTypes;
            Pageable paging = PageRequest.of(page, size, Sort.by(orders));

            Page<LeaveType> pageLeaveType;
            if(StringUtils.isEmpty(keywords) || StringUtils.isBlank(keywords)) {
                pageLeaveType = leaveTypeService.findAll(paging);
            } else {
                pageLeaveType = leaveTypeService.searchWithCriteria(keywords, paging);
            }

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
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
    public ResponseEntity create(@RequestBody LeaveTypeDTO leaveTypeDTO) {
        ResponseEntity response;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveTypeValidator.validateAdd(leaveTypeDTO);
            if(errorResponse == null) {
                final LeaveType leaveType = leaveTypeService.create(leaveTypeMapper.toEntity(leaveTypeDTO));
                response = ResponseEntity.status(HttpStatus.OK).body(leaveTypeMapper.toDto(leaveType));
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
        } catch (final Exception e) {
            logger.error("Error creating leave type with wording : "+leaveTypeDTO.getWording(), e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
    public ResponseEntity update(@PathVariable("id") UUID id, @RequestBody LeaveTypeDTO leaveTypeDTO) {
        ResponseEntity response;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveTypeValidator.validateUpdate(id, leaveTypeDTO);
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
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
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

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    private List<Sort.Order> getOrders(String[] sort) {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        if (sort[0].contains(",")) {
            // will sort more than 2 fields
            // sortOrder="field, direction"
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            // sort=[field, direction]
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }

}
