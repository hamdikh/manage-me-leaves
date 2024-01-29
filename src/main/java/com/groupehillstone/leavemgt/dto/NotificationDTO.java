package com.groupehillstone.leavemgt.dto;

import com.groupehillstone.leavemgt.enums.NotificationCategory;
import com.groupehillstone.leavemgt.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {

    private NotificationCategory category;

    private NotificationType type;

    private String validatorEmail;

    private String collaboratorEmail;

    private String collaboratorFirstName;

    private String collaboratorLastName;

}
