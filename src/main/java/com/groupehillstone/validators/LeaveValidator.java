package com.groupehillstone.validators;

import com.groupehillstone.leavemgt.dto.LeaveDTO;
import com.groupehillstone.leavemgt.dto.LeaveTypeDTO;
import com.groupehillstone.leavemgt.enums.LeaveType;
import com.groupehillstone.leavemgt.services.HolidayService;
import com.groupehillstone.utils.ErrorResponse;
import com.groupehillstone.utils.ErrorUtils;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LeaveValidator {

    private final Logger logger = LoggerFactory.getLogger(LeaveValidator.class);

    @Autowired
    private HolidayService holidayService;

    private final String regExpDate = "^\\d{4}-\\d{2}-\\d{2}$";

    public ErrorResponse validate(LeaveDTO leaveDTO) {
        ErrorResponse response = null;
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        try {
            if(leaveDTO != null) {
                LeaveTypeDTO type = leaveDTO.getType();
                List<LocalDate> leaveDays = leaveDTO.getLeaveDays();
                if(type == null) {
                    errors.add(new ErrorResponse.ValidationError("type", "TYPE_EMPTY"));
                }
                if(leaveDays.size() == 0) {
                    errors.add(new ErrorResponse.ValidationError("leaveDays", "LEAVE_DAYS_EMPTY"));
                } else {
                    List<LocalDate> holidaysList = null;
                    holidaysList = holidayService.enabledHolidaysDate();
                    for(LocalDate date : leaveDays) {
                        Pattern DATE_PATTERN = Pattern.compile(regExpDate);
                        Matcher matcher = DATE_PATTERN.matcher(date.toString());
                        if(!matcher.matches()) {
                            errors.add(new ErrorResponse.ValidationError("leaveDays", "LEAVE_DATE_INVALID"+date));
                        } else {
                            if(holidaysList.contains(date)) {
                            errors.add(new ErrorResponse.ValidationError("leaveDays", "LEAVE_DAYS_HOLIDAY "+date));
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
