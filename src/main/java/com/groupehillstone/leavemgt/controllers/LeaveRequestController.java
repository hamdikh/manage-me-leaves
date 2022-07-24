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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_BUSINESS')")
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
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_ADMIN')")
    public ResponseEntity getAllPageable(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size,
                                          @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String typeId,
                                         @RequestParam(required = false) String createdAt,
                                         @RequestParam(required = false) String businessUnitId,
                                         @RequestParam(defaultValue = "created_at,desc") String[] sort) {
        ResponseEntity responseEntity;
        try {
            List<Sort.Order> orders = getOrders(sort);
            List<LeaveRequestDTO> leaveRequests;
            Pageable paging = PageRequest.of(page, size, Sort.by(orders));

            boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
            boolean typeIdCheck = StringUtils.isEmpty(typeId) && StringUtils.isBlank(typeId);
            boolean createdAtCheck = StringUtils.isEmpty(createdAt) && StringUtils.isBlank(createdAt);
            boolean businessUnitIdCheck = StringUtils.isEmpty(businessUnitId) && StringUtils.isBlank(businessUnitId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate formattedCreatedAt = null;

            Page<LeaveRequest> pageLeaveRequests = null;
            if(statusCheck && typeIdCheck && createdAtCheck && businessUnitIdCheck) {
                pageLeaveRequests = leaveRequestService.findAll(paging);
            } else {
                UUID uuidType = null;
                UUID uuidBusinessUnitId = null;
                if(!createdAtCheck) {
                    formattedCreatedAt = LocalDate.parse(createdAt, formatter);
                }
                if(StringUtils.isNotBlank(typeId) && StringUtils.isNotEmpty(typeId)) {
                    uuidType = uuidMapper.stringToUUID(typeId);
                }
                if(!businessUnitIdCheck) {
                    uuidBusinessUnitId = uuidMapper.stringToUUID(businessUnitId);
                }
                pageLeaveRequests = leaveRequestService.searchWithCriteria(status, uuidType, formattedCreatedAt, uuidBusinessUnitId, paging);
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RH') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity getAllPageableForCollaborator(@PathVariable("id") UUID id,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "15") int size,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) String typeId,
                                            @RequestParam(required = false) String createdAt,
                                                        @RequestParam(required = false) String keywords,
                                                        @RequestParam(defaultValue = "created_at,desc") String[] sort) {
        ResponseEntity responseEntity;
        try {
            List<Sort.Order> orders = getOrders(sort);
            List<LeaveRequestDTO> leaveRequests;
            Pageable paging = PageRequest.of(page, size, Sort.by(orders));

            boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
            boolean typeIdCheck = StringUtils.isEmpty(typeId) && StringUtils.isBlank(typeId);
            boolean createdAtCheck = StringUtils.isEmpty(createdAt);
            boolean keywordsCheck = StringUtils.isEmpty(keywords) || StringUtils.isBlank(keywords);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate formattedCreatedAt = null;

            Page<LeaveRequest> pageLeaveRequests = null;
            if(statusCheck && typeIdCheck && createdAtCheck && keywordsCheck) {
                pageLeaveRequests = leaveRequestService.findLeaveRequestByCollaboratorId(id, paging);
            } else {
                UUID uuidType = null;
                if(!createdAtCheck) {
                    formattedCreatedAt = LocalDate.parse(createdAt, formatter);
                }
                if(StringUtils.isNotBlank(typeId) && StringUtils.isNotEmpty(typeId)) {
                    uuidType = uuidMapper.stringToUUID(typeId);
                }
                pageLeaveRequests = leaveRequestService.searchWithCriteriaForCollaborator(id, status, uuidType, formattedCreatedAt, keywords, paging);
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RH') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RH') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RH') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RH') or hasRole('ROLE_BUSINESS')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RH') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_EMPLOYEE')")
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

    @GetMapping("/for-sales/{id}")
    @PreAuthorize("hasRole('ROLE_BUSINESS')")
    public ResponseEntity getLeaveRequestsBySalesManagerId(@PathVariable("id") UUID id,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "15") int size,
                                                           @RequestParam(required = false) String status,
                                                           @RequestParam(required = false) String typeId,
                                                           @RequestParam(required = false) String createdAt,
                                                           @RequestParam(required = false) String businessUnitId,
                                                           @RequestParam(required = false) String keywords,
                                                           @RequestParam(defaultValue = "created_at,desc") String[] sort) {
        ResponseEntity responseEntity;
        try {
            List<Sort.Order> orders = getOrders(sort);
            List<LeaveRequestDTO> leaveRequests;
            Pageable paging = PageRequest.of(page, size, Sort.by(orders));

            boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
            boolean typeIdCheck = StringUtils.isEmpty(typeId) && StringUtils.isBlank(typeId);
            boolean createdAtCheck = StringUtils.isEmpty(createdAt) && StringUtils.isBlank(createdAt);
            boolean businessUnitIdCheck = StringUtils.isEmpty(businessUnitId) && StringUtils.isBlank(businessUnitId);
            boolean keywordsCheck = StringUtils.isEmpty(keywords) || StringUtils.isBlank(keywords);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate formattedCreatedAt = null;

            Page<LeaveRequest> pageLeaveRequests = null;
            if(statusCheck && typeIdCheck && createdAtCheck && businessUnitIdCheck && keywordsCheck) {
                pageLeaveRequests = leaveRequestService.findLeaveRequestsBySalesManagerId(id, paging);
            } else {
                UUID uuidType = null;
                UUID uuidBusinessUnit = null;
                if(!createdAtCheck) {
                    formattedCreatedAt = LocalDate.parse(createdAt, formatter);
                }
                if(StringUtils.isNotEmpty(typeId) && StringUtils.isNotBlank(typeId)) {
                    uuidType = uuidMapper.stringToUUID(typeId);
                }
                if(!businessUnitIdCheck) {
                    uuidBusinessUnit = uuidMapper.stringToUUID(businessUnitId);
                }
                pageLeaveRequests = leaveRequestService.searchWithCriteriaForSales(id, status, uuidType, formattedCreatedAt, uuidBusinessUnit, keywords, paging);
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

    @GetMapping("/for-manager/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RH')")
    public ResponseEntity getLeaveRequestsByManagerId(@PathVariable("id") UUID id,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "15") int size,
                                                           @RequestParam(required = false) String status,
                                                           @RequestParam(required = false) String typeId,
                                                           @RequestParam(required = false) String createdAt,
                                                            @RequestParam(required = false) String keywords,
                                                            @RequestParam(required = false) String businessUnitId,
                                                      @RequestParam(defaultValue = "created_at,desc") String[] sort) {
        ResponseEntity responseEntity;
        try {
            List<Sort.Order> orders = getOrders(sort);
            List<LeaveRequestDTO> leaveRequests;
            Pageable paging = PageRequest.of(page, size, Sort.by(orders));

            boolean statusCheck = StringUtils.isEmpty(status) && StringUtils.isBlank(status);
            boolean typeIdCheck = StringUtils.isEmpty(typeId) && StringUtils.isBlank(typeId);
            boolean createdAtCheck = StringUtils.isEmpty(createdAt) && StringUtils.isBlank(createdAt);
            boolean businessUnitIdCheck = StringUtils.isEmpty(businessUnitId) && StringUtils.isBlank(businessUnitId);
            boolean keywordsCheck = StringUtils.isEmpty(keywords) || StringUtils.isBlank(keywords);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate formattedCreatedAt = null;

            Page<LeaveRequest> pageLeaveRequests = null;
            if(statusCheck && typeIdCheck && createdAtCheck && businessUnitIdCheck && keywordsCheck) {
                pageLeaveRequests = leaveRequestService.findLeaveRequestsByManagerId(id, paging);
            } else {
                UUID uuidType = null;
                UUID uuidBusinessUnitId = null;
                if(!createdAtCheck) {
                    formattedCreatedAt = LocalDate.parse(createdAt, formatter);
                }
                if(StringUtils.isNotEmpty(typeId) && StringUtils.isNotBlank(typeId)) {
                    uuidType = uuidMapper.stringToUUID(typeId);
                }
                if(!businessUnitIdCheck) {
                    uuidBusinessUnitId = uuidMapper.stringToUUID(businessUnitId);
                }
                pageLeaveRequests = leaveRequestService.searchWithCriteriaForManager(id, status, uuidType, formattedCreatedAt, uuidBusinessUnitId, keywords, paging);
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

    @GetMapping("/calendar/business-unit-manager/{id}")
    @PreAuthorize("hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_RH')")
    public ResponseEntity getLeaveRequestsByCollaboratorId(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final List<LeaveRequestDTO> leaveRequests = leaveRequestMapper.toDto(leaveRequestService.findLeaveRequestsByCollaboratorId(id));
            response = ResponseEntity.status(HttpStatus.OK).body(leaveRequests);
        } catch (final Exception e) {
            logger.error("Error retrieving leave request list for collaborator with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/calendar/business-unit/{id}")
    @PreAuthorize("hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_RH')")
    public ResponseEntity getLeaveRequestsByBusinessUnitId(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final List<LeaveRequestDTO> leaveRequests = leaveRequestMapper.toDto(leaveRequestService.findLeaveRequestsByBusinessUnitId(id));
            response = ResponseEntity.status(HttpStatus.OK).body(leaveRequests);
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests list by business unit id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/calendar/team-teamLeader/{id}")
    @PreAuthorize("hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_RH')")
    public ResponseEntity getLeaveRequestsByTeamId(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final List<LeaveRequestDTO> leaveRequests = leaveRequestMapper.toDto(leaveRequestService.findLeaveRequestsByTeamId(id));
            response = ResponseEntity.status(HttpStatus.OK).body(leaveRequests);
        } catch (final Exception e) {
            logger.error("Error retrieving leave request list by team id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/calendar/team/{id}")
    @PreAuthorize("hasRole('ROLE_BUSINESS') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_RH')")
    public ResponseEntity getLeaveRequestsForTeam(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final List<LeaveRequestDTO> leaveRequests = leaveRequestMapper.toDto(leaveRequestService.findLeaveRequestsForTeam(id));
            response = ResponseEntity.status(HttpStatus.OK).body(leaveRequests);
        } catch (final Exception e) {
            logger.error("Error retrieving leave requests for team with id : "+id, e);
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
