package com.groupehillstone.validators;

import com.groupehillstone.leavemgt.dto.CollaboratorDTO;
import com.groupehillstone.leavemgt.dto.LeaveDTO;
import com.groupehillstone.leavemgt.dto.LeaveRequestDTO;
import com.groupehillstone.leavemgt.dto.ResponseDTO;
import com.groupehillstone.leavemgt.entities.Collaborator;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.enums.LeaveType;
import com.groupehillstone.leavemgt.identity.IdentityRole;
import com.groupehillstone.leavemgt.mapper.CollaboratorMapper;
import com.groupehillstone.leavemgt.mapper.UUIDMapper;
import com.groupehillstone.leavemgt.services.CollaboratorService;
import com.groupehillstone.leavemgt.services.LeaveRequestService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.ErrorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeaveRequestValidator {

    private final Logger logger = LoggerFactory.getLogger(LeaveRequestValidator.class);

    @Autowired
    private UUIDMapper uuidMapper;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private CollaboratorMapper collaboratorMapper;

    @Autowired
    private CollaboratorService collaboratorService;

    public ErrorResponse validateCreate(LeaveRequestDTO leaveRequestDTO) {
        ErrorResponse response = null;
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        try {
            if(leaveRequestDTO != null) {
                String status = leaveRequestDTO.getStatus();
                CollaboratorDTO collaboratorDTO = leaveRequestDTO.getCollaborator();
                List<LeaveDTO> leaves = leaveRequestDTO.getLeaves();
                if(StringUtils.isEmpty(status) || StringUtils.isBlank(status)) {
                    errors.add(new ErrorResponse.ValidationError("status", "STATUS_EMPTY"));
                } else {
                    if(!EnumUtils.isValidEnum(LeaveStatus.class, status)) {
                        errors.add(new ErrorResponse.ValidationError("status", "INVALID_STATUS"));
                    }
                }
                if(collaboratorDTO == null) {
                    errors.add(new ErrorResponse.ValidationError("collaborator", "COLLABORATOR_EMPTY"));
                }
                if(leaves.size() == 0) {
                    errors.add(new ErrorResponse.ValidationError("leaves", "LEAVES_EMPTY"));
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
                String status = leaveRequestDTO.getStatus();
                CollaboratorDTO collaboratorDTO = leaveRequestDTO.getCollaborator();
                List<LeaveDTO> leaves = leaveRequestDTO.getLeaves();
                if(LeaveStatus.DRAFT.equals(leaveRequest.getStatus())) {
                    if(StringUtils.isEmpty(status) || StringUtils.isBlank(status)) {
                        errors.add(new ErrorResponse.ValidationError("status", "STATUS_EMPTY"));
                    } else {
                        if(!EnumUtils.isValidEnum(LeaveStatus.class, status)) {
                            errors.add(new ErrorResponse.ValidationError("status", "INVALID_STATUS"));
                        }
                    }
                    if(collaboratorDTO == null) {
                        errors.add(new ErrorResponse.ValidationError("collaborator", "COLLABORATOR_EMPTY"));
                    }
                    if(leaves.size() == 0) {
                        errors.add(new ErrorResponse.ValidationError("leaves", "LEAVES_EMPTY"));
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
                                CollaboratorDTO collaboratorDTO = leaveRequestDTO.getCollaborator();
                                Collaborator collaborator = collaboratorService.findCollaboratorById(uuidMapper.stringToUUID(collaboratorDTO.getId()));
                                CollaboratorDTO validatorDTO = leaveRequestDTO.getValidator();
                                Collaborator validator = collaboratorService.findCollaboratorById(uuidMapper.stringToUUID(validatorDTO.getId()));
                                List<LeaveDTO> leaves = leaveRequestDTO.getLeaves();
                                if(collaboratorDTO == null) {
                                    errors.add(new ErrorResponse.ValidationError("collaborator", "COLLABORATOR_EMPTY"));
                                }
                                if(validatorDTO == null) {
                                    errors.add(new ErrorResponse.ValidationError("validator", "VALIDATOR_EMPTY"));
                                } else {
                                    if(collaborator.getIdentityRole().equals(IdentityRole.TEAM_MANAGER) || collaborator.getIdentityRole().equals(IdentityRole.EMPLOYEE)) {
                                        if(!validator.getIdentityRole().equals(IdentityRole.RH) && !(validator.getIdentityRole().equals(IdentityRole.BUSINESS) && collaborator.getSalesManager().getId().equals(validator.getId()))) {
                                            errors.add(new ErrorResponse.ValidationError("validator", "VALIDATOR_NOT_ALLOWED"));
                                        }
                                    } else {
                                        if(!validator.getIdentityRole().equals(IdentityRole.RH) && !(validator.getIdentityRole().equals(IdentityRole.ADMIN) && collaborator.getManager().equals(validator) )) {
                                            errors.add(new ErrorResponse.ValidationError("validator", "VALIDATOR_NOT_ALLOWED"));
                                        }
                                    }
                                }
                                if(leaves.size() == 0) {
                                    errors.add(new ErrorResponse.ValidationError("leaves", "LEAVES_EMPTY"));
                                }
                            } else {
                                errors.add(new ErrorResponse.ValidationError("validator", "REQUEST_MAX_VALIDATORS"));
                            }
                        } else {
                            if(leaveRequest.getFirstValidator() == null || leaveRequest.getSecondValidator() == null) {
                                CollaboratorDTO collaboratorDTO = leaveRequestDTO.getCollaborator();
                                Collaborator collaborator = collaboratorService.findCollaboratorById(uuidMapper.stringToUUID(collaboratorDTO.getId()));
                                CollaboratorDTO validatorDTO = leaveRequestDTO.getValidator();
                                Collaborator validator = collaboratorService.findCollaboratorById(uuidMapper.stringToUUID(validatorDTO.getId()));
                                List<LeaveDTO> leaves = leaveRequestDTO.getLeaves();
                                if(collaboratorDTO == null) {
                                    errors.add(new ErrorResponse.ValidationError("collaborator", "COLLABORATOR_EMPTY"));
                                }
                                if(validatorDTO == null) {
                                    errors.add(new ErrorResponse.ValidationError("validator", "VALIDATOR_EMPTY"));
                                } else {
                                    if(collaborator.getIdentityRole().equals(IdentityRole.TEAM_MANAGER.toString()) || collaborator.getIdentityRole().equals(IdentityRole.EMPLOYEE.toString())) {
                                        if(!validator.getIdentityRole().equals(IdentityRole.RH.toString()) && !(validator.getIdentityRole().equals(IdentityRole.BUSINESS) && collaborator.getSalesManager().getId().equals(validator.getId()))) {
                                            errors.add(new ErrorResponse.ValidationError("validator", "VALIDATOR_NOT_ALLOWED"));
                                        }
                                    } else {
                                        if(!validator.getIdentityRole().equals(IdentityRole.RH.toString()) && !(validator.getIdentityRole().equals(IdentityRole.ADMIN.toString()) && collaborator.getManager().equals(validator) )) {
                                            errors.add(new ErrorResponse.ValidationError("validator", "VALIDATOR_NOT_ALLOWED"));
                                        }
                                    }
                                }
                                if(leaves.size() == 0) {
                                    errors.add(new ErrorResponse.ValidationError("leaves", "LEAVES_EMPTY"));
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
