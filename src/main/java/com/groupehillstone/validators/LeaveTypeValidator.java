package com.groupehillstone.validators;

import com.groupehillstone.leavemgt.dto.LeaveTypeDTO;
import com.groupehillstone.leavemgt.entities.LeaveType;
import com.groupehillstone.leavemgt.mapper.LeaveTypeMapper;
import com.groupehillstone.leavemgt.services.LeaveTypeService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.ErrorUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class LeaveTypeValidator {

    private final Logger logger = LoggerFactory.getLogger(LeaveRequestValidator.class);

    @Autowired
    private LeaveTypeMapper leaveTypeMapper;

    @Autowired
    private LeaveTypeService leaveTypeService;


    public ErrorResponse validateAdd(LeaveTypeDTO leaveTypeDTO) {
        ErrorResponse response = null;
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        try {
            if(leaveTypeDTO != null) {
                String name = leaveTypeDTO.getWording();
                String code = leaveTypeDTO.getCode();
                if( StringUtils.isEmpty(name) || StringUtils.isBlank(name)) {
                    errors.add(new ErrorResponse.ValidationError("name", "WORDING_EMPTY"));
                } else if(StringUtils.isNotEmpty(name) && leaveTypeService.existsByName(name)) {
                    errors.add(new ErrorResponse.ValidationError("name", "WORDING_EXISTS"));
                }
                if( StringUtils.isEmpty(code) || StringUtils.isBlank(code)) {
                    errors.add(new ErrorResponse.ValidationError("code", "CODE_EMPTY"));
                } else {
                    final String toUpperCaseCode = leaveTypeDTO.getCode().toUpperCase();
                    if (leaveTypeService.existsByCode(toUpperCaseCode)){
                        errors.add(new ErrorResponse.ValidationError("code", "CODE_EXISTS"));
                    }
                }
                if (!CollectionUtils.isEmpty(errors)) {
                    response = ErrorUtils.buildErrorResponseValidator("VALIDATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, errors);
                }
            }
        } catch (final Exception e) {
            logger.error("ERROR_VALIDATING", e);
        }
        return response;
    }

    public ErrorResponse validateUpdate(UUID id, LeaveTypeDTO leaveTypeDTO) {
        ErrorResponse response = null;
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        try {
            if(leaveTypeDTO != null) {
                final LeaveType leaveType = leaveTypeService.findById(id);
                if(leaveTypeDTO.getWording() == null || leaveTypeDTO.getWording().isEmpty()) {
                    errors.add(new ErrorResponse.ValidationError("name", "WORDING_EMPTY"));
                } else if(StringUtils.isNotEmpty(leaveTypeDTO.getWording()) && !leaveType.getWording().equals(leaveTypeDTO.getWording())
                        && leaveTypeService.existsByName(leaveTypeDTO.getWording())) {
                    errors.add(new ErrorResponse.ValidationError("name", "WORDING_EXISTS"));
                }
                if(leaveTypeDTO.getCode() == null || leaveTypeDTO.getCode().isEmpty()) {
                    errors.add(new ErrorResponse.ValidationError("code", "CODE_EMPTY"));
                } else if(!leaveTypeDTO.getCode().isEmpty() && !leaveType.getCode().equals(leaveTypeDTO.getCode())){
                    errors.add(new ErrorResponse.ValidationError("code", "CODE_NOT_UPDATABLE"));
                }
                if (!CollectionUtils.isEmpty(errors)) {
                    response = ErrorUtils.buildErrorResponseValidator("VALIDATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, errors);
                }
            }
        } catch (final Exception e) {
            logger.error("ERROR_VALIDATING", e);
        }
        return response;
    }

}
