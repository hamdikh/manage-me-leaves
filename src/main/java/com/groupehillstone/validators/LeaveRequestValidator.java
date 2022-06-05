package com.groupehillstone.validators;

import com.groupehillstone.leavemgt.dto.CollaboratorDTO;
import com.groupehillstone.leavemgt.dto.LeaveRequestDTO;
import com.groupehillstone.leavemgt.dto.ResponseDTO;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.enums.LeaveType;
import com.groupehillstone.leavemgt.mapper.UUIDMapper;
import com.groupehillstone.leavemgt.services.LeaveRequestService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.ErrorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LeaveRequestValidator {

    private final Logger logger = LoggerFactory.getLogger(LeaveRequestValidator.class);

    @Autowired
    private UUIDMapper uuidMapper;

    @Autowired
    private LeaveRequestService leaveRequestService;

    private final String regExpDate = "^\\d{4}-\\d{2}-\\d{2}$";

    public ErrorResponse validateCreate(LeaveRequestDTO leaveRequestDTO) {
        ErrorResponse response = null;
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        try {
            if(leaveRequestDTO != null) {
                String type = leaveRequestDTO.getType();
                String status = leaveRequestDTO.getStatus();
                LocalDate startDate = leaveRequestDTO.getStartDate();
                LocalDate endDate = leaveRequestDTO.getEndDate();
                CollaboratorDTO collaboratorDTO = leaveRequestDTO.getCollaborator();
                if(StringUtils.isEmpty(type) || StringUtils.isBlank(type)) {
                    errors.add(new ErrorResponse.ValidationError("type", "TYPE_EMPTY"));
                } else {
                    if(!EnumUtils.isValidEnum(LeaveType.class, type)) {
                        errors.add(new ErrorResponse.ValidationError("type", "INVALID_TYPE"));
                    }
                }
                if(StringUtils.isEmpty(status) || StringUtils.isBlank(status)) {
                    errors.add(new ErrorResponse.ValidationError("status", "STATUS_EMPTY"));
                } else {
                    if(!EnumUtils.isValidEnum(LeaveStatus.class, status)) {
                        errors.add(new ErrorResponse.ValidationError("status", "INVALID_STATUS"));
                    }
                }
                if(startDate == null) {
                    errors.add(new ErrorResponse.ValidationError("startDate", "START_DATE_EMPTY"));
                } else {
                    Pattern START_DATE_PATTERN = Pattern.compile(regExpDate);
                    Matcher matcher = START_DATE_PATTERN.matcher(startDate.toString());
                    if(!matcher.matches()) {
                        errors.add(new ErrorResponse.ValidationError("startDate", "START_DATE_INVALID"));
                    }
                }
                if(endDate == null) {
                    errors.add(new ErrorResponse.ValidationError("endDate", "END_DATE_EMPTY"));
                } else {
                    Pattern END_DATE_PATTERN = Pattern.compile(regExpDate);
                    Matcher matcher = END_DATE_PATTERN.matcher(startDate.toString());
                    if(!matcher.matches()) {
                        errors.add(new ErrorResponse.ValidationError("endDate", "END_DATE_INVALID"));
                    }
                }
                if(collaboratorDTO == null) {
                    errors.add(new ErrorResponse.ValidationError("collaborator", "COLLABORATOR_EMPTY"));
                }
            }
            if(!CollectionUtils.isEmpty(errors)) {
                response = ErrorUtils.buildErrorResponseValidator("VALIDATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, errors);
            }
        } catch (final Exception e) {
            logger.error("ERROR_VALIDATING", e);
        }
        return response;
    }

    public ErrorResponse validateUpdate(LeaveRequestDTO leaveRequestDTO) {
        ErrorResponse response = null;
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        try {
            if(leaveRequestDTO != null) {
                LeaveRequest leaveRequest = leaveRequestService.findById(uuidMapper.stringToUUID(leaveRequestDTO.getId()));
                String type = leaveRequestDTO.getType();
                String status = leaveRequestDTO.getStatus();
                LocalDate startDate = leaveRequestDTO.getStartDate();
                LocalDate endDate = leaveRequestDTO.getEndDate();
                CollaboratorDTO collaboratorDTO = leaveRequestDTO.getCollaborator();
                if(LeaveStatus.DRAFT.equals(leaveRequest.getStatus())) {
                    if(StringUtils.isEmpty(type) || StringUtils.isBlank(type)) {
                        errors.add(new ErrorResponse.ValidationError("type", "TYPE_EMPTY"));
                    } else {
                        if(!EnumUtils.isValidEnum(LeaveType.class, type)) {
                            errors.add(new ErrorResponse.ValidationError("type", "INVALID_TYPE"));
                        }
                    }
                    if(StringUtils.isEmpty(status) || StringUtils.isBlank(status)) {
                        errors.add(new ErrorResponse.ValidationError("status", "STATUS_EMPTY"));
                    } else {
                        if(!EnumUtils.isValidEnum(LeaveStatus.class, status)) {
                            errors.add(new ErrorResponse.ValidationError("status", "INVALID_STATUS"));
                        }
                    }
                    if(startDate == null) {
                        errors.add(new ErrorResponse.ValidationError("startDate", "START_DATE_EMPTY"));
                    } else {
                        Pattern START_DATE_PATTERN = Pattern.compile(regExpDate);
                        Matcher matcher = START_DATE_PATTERN.matcher(startDate.toString());
                        if(!matcher.matches()) {
                            errors.add(new ErrorResponse.ValidationError("startDate", "START_DATE_INVALID"));
                        }
                    }
                    if(endDate == null) {
                        errors.add(new ErrorResponse.ValidationError("endDate", "END_DATE_EMPTY"));
                    } else {
                        Pattern END_DATE_PATTERN = Pattern.compile(regExpDate);
                        Matcher matcher = END_DATE_PATTERN.matcher(startDate.toString());
                        if(!matcher.matches()) {
                            errors.add(new ErrorResponse.ValidationError("endDate", "END_DATE_INVALID"));
                        }
                    }
                    if(collaboratorDTO == null) {
                        errors.add(new ErrorResponse.ValidationError("collaborator", "COLLABORATOR_EMPTY"));
                    }
                }
            }
            if(!CollectionUtils.isEmpty(errors)) {
                response = ErrorUtils.buildErrorResponseValidator("VALIDATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, errors);
            }
        } catch (final Exception e) {
            logger.error("ERROR_VALIDATING", e);
        }
        return response;
    }

    public ErrorResponse validateResponse(ResponseDTO leaveRequestDTO) {
        ErrorResponse response = null;
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        try {
            if(leaveRequestDTO != null) {
                String status = leaveRequestDTO.getStatus();
                LeaveRequest leaveRequest = leaveRequestService.findById(uuidMapper.stringToUUID(leaveRequestDTO.getId()));
                if(StringUtils.isEmpty(status) || StringUtils.isBlank(status)) {
                    errors.add(new ErrorResponse.ValidationError("status", "STATUS_EMPTY"));
                } else {
                    if(!EnumUtils.isValidEnum(LeaveStatus.class, status)) {
                        errors.add(new ErrorResponse.ValidationError("status", "INVALID_STATUS"));
                    } else {
                        if(LeaveStatus.VALIDATED.toString().equals(leaveRequestDTO.getStatus())) {
                            if(leaveRequest.getFirstValidator() == null || leaveRequest.getSecondValidator() == null) {
                                String type = leaveRequestDTO.getType();
                                LocalDate startDate = leaveRequestDTO.getStartDate();
                                LocalDate endDate = leaveRequestDTO.getEndDate();
                                CollaboratorDTO collaboratorDTO = leaveRequestDTO.getCollaborator();
                                CollaboratorDTO validator = leaveRequestDTO.getValidator();
                                if(StringUtils.isEmpty(type) || StringUtils.isBlank(type)) {
                                    errors.add(new ErrorResponse.ValidationError("type", "TYPE_EMPTY"));
                                } else {
                                    if(!EnumUtils.isValidEnum(LeaveType.class, type)) {
                                        errors.add(new ErrorResponse.ValidationError("type", "INVALID_TYPE"));
                                    }
                                }
                                if(startDate == null) {
                                    errors.add(new ErrorResponse.ValidationError("startDate", "START_DATE_EMPTY"));
                                } else {
                                    Pattern START_DATE_PATTERN = Pattern.compile(regExpDate);
                                    Matcher matcher = START_DATE_PATTERN.matcher(startDate.toString());
                                    if(!matcher.matches()) {
                                        errors.add(new ErrorResponse.ValidationError("startDate", "START_DATE_INVALID"));
                                    }
                                }
                                if(endDate == null) {
                                    errors.add(new ErrorResponse.ValidationError("endDate", "END_DATE_EMPTY"));
                                } else {
                                    Pattern END_DATE_PATTERN = Pattern.compile(regExpDate);
                                    Matcher matcher = END_DATE_PATTERN.matcher(startDate.toString());
                                    if(!matcher.matches()) {
                                        errors.add(new ErrorResponse.ValidationError("endDate", "END_DATE_INVALID"));
                                    }
                                }
                                if(collaboratorDTO == null) {
                                    errors.add(new ErrorResponse.ValidationError("collaborator", "COLLABORATOR_EMPTY"));
                                }
                                if(validator == null) {
                                    errors.add(new ErrorResponse.ValidationError("validator", "VALIDATOR_EMPTY"));
                                }
                            } else {
                                errors.add(new ErrorResponse.ValidationError("validator", "REQUEST_MAX_VALIDATORS"));
                            }
                        } else {
                            if(leaveRequest.getFirstValidator() == null || leaveRequest.getSecondValidator() == null) {
                                String type = leaveRequestDTO.getType();
                                LocalDate startDate = leaveRequestDTO.getStartDate();
                                LocalDate endDate = leaveRequestDTO.getEndDate();
                                CollaboratorDTO collaboratorDTO = leaveRequestDTO.getCollaborator();
                                CollaboratorDTO validator = leaveRequestDTO.getValidator();
                                if(StringUtils.isEmpty(type) || StringUtils.isBlank(type)) {
                                    errors.add(new ErrorResponse.ValidationError("type", "TYPE_EMPTY"));
                                } else {
                                    if(!EnumUtils.isValidEnum(LeaveType.class, type)) {
                                        errors.add(new ErrorResponse.ValidationError("type", "INVALID_TYPE"));
                                    }
                                }
                                if(startDate == null) {
                                    errors.add(new ErrorResponse.ValidationError("startDate", "START_DATE_EMPTY"));
                                } else {
                                    Pattern START_DATE_PATTERN = Pattern.compile(regExpDate);
                                    Matcher matcher = START_DATE_PATTERN.matcher(startDate.toString());
                                    if(!matcher.matches()) {
                                        errors.add(new ErrorResponse.ValidationError("startDate", "START_DATE_INVALID"));
                                    }
                                }
                                if(endDate == null) {
                                    errors.add(new ErrorResponse.ValidationError("endDate", "END_DATE_EMPTY"));
                                } else {
                                    Pattern END_DATE_PATTERN = Pattern.compile(regExpDate);
                                    Matcher matcher = END_DATE_PATTERN.matcher(startDate.toString());
                                    if(!matcher.matches()) {
                                        errors.add(new ErrorResponse.ValidationError("endDate", "END_DATE_INVALID"));
                                    }
                                }
                                if(collaboratorDTO == null) {
                                    errors.add(new ErrorResponse.ValidationError("collaborator", "COLLABORATOR_EMPTY"));
                                }
                                if(validator == null) {
                                    errors.add(new ErrorResponse.ValidationError("validator", "VALIDATOR_EMPTY"));
                                }
                            }
                        }
                    }
                }
            }
            if(!CollectionUtils.isEmpty(errors)) {
                response = ErrorUtils.buildErrorResponseValidator("VALIDATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, errors);
            }
        } catch (final Exception e) {
            logger.error("ERROR_VALIDATING", e);
        }
        return response;
    }

}
