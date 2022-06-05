package com.groupehillstone.leavemgt.controllers;

import com.groupehillstone.leavemgt.dto.LeaveRequestDTO;
import com.groupehillstone.leavemgt.dto.ResponseDTO;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.mapper.LeaveRequestMapper;
import com.groupehillstone.leavemgt.mapper.UUIDMapper;
import com.groupehillstone.leavemgt.services.LeaveRequestService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.SuccessResponse;
import com.groupehillstone.validators.LeaveRequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/leave-requests")
@Slf4j
public class LeaveRequestController {

    private final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LeaveRequestValidator leaveRequestValidator;

    @Autowired
    private UUIDMapper uuidMapper;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS')")
    public ResponseEntity getAll() {
        ResponseEntity response;
        try {
            final List<LeaveRequest> leaveRequests = leaveRequestService.findAll();
            response = ResponseEntity.status(HttpStatus.OK).body(leaveRequestMapper.toDto(leaveRequests));
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests list ", e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS')")
    public ResponseEntity getAllPageable(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size,
                                          @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String type,
                                         @RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate) {
        ResponseEntity responseEntity;
        try {
            List<LeaveRequestDTO> leaveRequests;
            Pageable paging = PageRequest.of(page, size);

            boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
            boolean typeCheck = StringUtils.isEmpty(type) && StringUtils.isBlank(type);
            boolean startDateCheck = StringUtils.isEmpty(startDate) && StringUtils.isBlank(startDate);
            boolean endDateCheck = StringUtils.isEmpty(endDate) && StringUtils.isBlank(endDate);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate formattedStartDate = null;
            LocalDate formatterEndDate = null;

            Page<LeaveRequest> pageLeaveRequests = null;
            if(statusCheck && typeCheck && startDateCheck && endDateCheck) {
                pageLeaveRequests = leaveRequestService.findAll(paging);
            } else {
                if(!startDateCheck) {
                    formattedStartDate = LocalDate.parse(startDate, formatter);
                }
                if(!endDateCheck) {
                    formatterEndDate = LocalDate.parse(endDate, formatter);
                }
                pageLeaveRequests = new PageImpl<>(leaveRequestService.searchWithCriteria(status, type, formattedStartDate, formatterEndDate));
            }

            leaveRequests = leaveRequestMapper.toDto(pageLeaveRequests.getContent());
            Map<String, Object> response = new HashMap<>();
            response.put("leaveRequests", leaveRequests);
            response.put("currentPage", pageLeaveRequests.getNumber());
            response.put("totalItems", pageLeaveRequests.getTotalElements());
            response.put("totalPages", pageLeaveRequests.getTotalPages());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (final Exception e) {
            logger.error("Error retrieving list ", e);
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return responseEntity;
    }

    @GetMapping("/all/collaborator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity getAllPageableForCollaborator(@PathVariable("id") UUID id,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "15") int size,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) String type,
                                            @RequestParam(required = false) String startDate,
                                            @RequestParam(required = false) String endDate) {
        ResponseEntity responseEntity;
        try {
            List<LeaveRequestDTO> leaveRequests;
            Pageable paging = PageRequest.of(page, size);

            boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
            boolean typeCheck = StringUtils.isEmpty(type) && StringUtils.isBlank(type);
            boolean startDateCheck = StringUtils.isEmpty(startDate) && StringUtils.isBlank(startDate);
            boolean endDateCheck = StringUtils.isEmpty(endDate) && StringUtils.isBlank(endDate);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate formattedStartDate = null;
            LocalDate formatterEndDate = null;

            Page<LeaveRequest> pageLeaveRequests = null;
            if(statusCheck && typeCheck && startDateCheck && endDateCheck) {
                pageLeaveRequests = leaveRequestService.findLeaveRequestByCollaboratorId(id, paging);
            } else {
                if(!startDateCheck) {
                    formattedStartDate = LocalDate.parse(startDate, formatter);
                }
                if(!endDateCheck) {
                    formatterEndDate = LocalDate.parse(endDate, formatter);
                }
                pageLeaveRequests = new PageImpl<>(leaveRequestService.searchWithCriteriaForCollaborator(id, status, type, formattedStartDate, formatterEndDate));
            }

            leaveRequests = leaveRequestMapper.toDto(pageLeaveRequests.getContent());
            Map<String, Object> response = new HashMap<>();
            response.put("leaveRequests", leaveRequests);
            response.put("currentPage", pageLeaveRequests.getNumber());
            response.put("totalItems", pageLeaveRequests.getTotalElements());
            response.put("totalPages", pageLeaveRequests.getTotalPages());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (final Exception e) {
            logger.error("Error retrieving list ", e);
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return responseEntity;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity getById(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final LeaveRequestDTO leaveRequest = leaveRequestMapper.toDto(leaveRequestService.findById(id));
            response = ResponseEntity.status(HttpStatus.OK).body(leaveRequest);
        } catch (final Exception e) {
            logger.error("Error retrieving leave request with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity create(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        ResponseEntity response;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveRequestValidator.validateCreate(leaveRequestDTO);
            if(errorResponse == null) {
                final LeaveRequest leaveRequest = leaveRequestService.create(leaveRequestMapper.toEntity(leaveRequestDTO));
                response = ResponseEntity.status(HttpStatus.OK).body(leaveRequestMapper.toDto(leaveRequest));
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
        } catch (final Exception e) {
            logger.error("Error creating leave request for : "+leaveRequestDTO.getCollaborator().getFirstName(), e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity update(@PathVariable("id") UUID id, @RequestBody LeaveRequestDTO leaveRequestDTO) {
        ResponseEntity response = null;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveRequestValidator.validateUpdate(leaveRequestDTO);
            if(errorResponse == null) {
                final LeaveRequest leaveRequest = leaveRequestService.update(leaveRequestMapper.toEntity(leaveRequestDTO));
                response = ResponseEntity.status(HttpStatus.OK).body(leaveRequestMapper.toDto(leaveRequest));
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
        } catch (final Exception e) {
            logger.error("Error updating leave request with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @PutMapping("/response/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS')")
    public ResponseEntity response(@PathVariable("id") UUID id, @RequestBody ResponseDTO responseDTO) {
        ResponseEntity response = null;
        ErrorResponse errorResponse = null;
        try {
            errorResponse = leaveRequestValidator.validateResponse(responseDTO);
            if(errorResponse == null) {
                LeaveRequest leaveRequest = leaveRequestService.response(id, responseDTO);
                response = ResponseEntity.status(HttpStatus.OK).body(leaveRequestMapper.toDto(leaveRequest));
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
        } catch (final Exception e) {
            logger.error("Error responding- leave request with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity delete(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            leaveRequestService.delete(id);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("DELETED"));
        } catch (final Exception e) {
            logger.error("Error deleting leave request with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }


}
