package com.groupehillstone.validators;

import com.groupehillstone.leavemgt.dto.LeaveTypeDTO;
import com.groupehillstone.leavemgt.mapper.LeaveTypeMapper;
import com.groupehillstone.leavemgt.services.LeaveTypeService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.ErrorUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeaveTypeValidator {

    private final Logger logger = LoggerFactory.getLogger(LeaveRequestValidator.class);

    @Autowired
    private LeaveTypeMapper leaveTypeMapper;

    @Autowired
    private LeaveTypeService leaveTypeService;


    public ErrorResponse validate(LeaveTypeDTO leaveTypeDTO) {
        ErrorResponse response = null;
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        try {
            if(leaveTypeDTO != null) {
                String name = leaveTypeDTO.getName();
                if(StringUtils.isEmpty(name) || StringUtils.isBlank(name)) {
                    errors.add(new ErrorResponse.ValidationError("name", "NAME_EMPTY"));
                } else {
                    if(leaveTypeService.existsByName(name)) {
                        errors.add(new ErrorResponse.ValidationError("name", "NAME_EXISTS"));
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
