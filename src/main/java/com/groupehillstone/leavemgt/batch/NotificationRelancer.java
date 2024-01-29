package com.groupehillstone.leavemgt.batch;

import com.groupehillstone.config.NotificationConfig;
import com.groupehillstone.leavemgt.dto.NotificationDTO;
import com.groupehillstone.leavemgt.entities.Collaborator;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.enums.NotificationCategory;
import com.groupehillstone.leavemgt.enums.NotificationType;
import com.groupehillstone.leavemgt.identity.IdentityRole;
import com.groupehillstone.leavemgt.services.CollaboratorService;
import com.groupehillstone.leavemgt.services.LeaveRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@EnableScheduling
public class NotificationRelancer {

    private final Logger logger = LoggerFactory.getLogger(NotificationRelancer.class);

    @Autowired
    private NotificationConfig notificationConfig;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private CollaboratorService collaboratorService;

    public static void main(String[] args) {
        SpringApplication.run(NotificationRelancer.class, args);
    }

    @Scheduled(cron = "${manageme.notifications.schedule}")
    public void relance() {
        final List<LeaveRequest> leaveRequests = leaveRequestService.findNotValidated();
        for(LeaveRequest leaveRequest : leaveRequests) {
            if(LeaveStatus.PENDING.equals(leaveRequest.getStatus())) {
                notifyManager(leaveRequest);
                notifyRH(leaveRequest);
            } else if(LeaveStatus.PARTIALLY_VALIDATED.equals(leaveRequest.getStatus())) {
                IdentityRole identityRole = leaveRequest.getFirstValidator().getIdentityRole();
                if(IdentityRole.RH.equals(identityRole)) {
                    notifyManager(leaveRequest);
                } else {
                    notifyRH(leaveRequest);
                }
            }
        }
    }

    private void notifyManager(LeaveRequest leaveRequest) {
        RestTemplate restTemplate = new RestTemplate();
        NotificationDTO request = new NotificationDTO();
        request.setCategory(NotificationCategory.LEAVE_REQUEST);
        request.setType(NotificationType.RELANCE);
        Collaborator collaborator = leaveRequest.getCollaborator();
        if(collaborator.getSalesManager() == null) {
            request.setValidatorEmail(leaveRequest.getCollaborator().getManager().getEmail());
        } else {
            request.setValidatorEmail(leaveRequest.getCollaborator().getSalesManager().getEmail());
        }
        request.setCollaboratorFirstName(collaborator.getFirstName());
        request.setCollaboratorLastName(collaborator.getLastName());
        try {
            restTemplate.postForObject(notificationConfig.getLeaveRequestsUrl(), request, ResponseEntity.class);
        } catch (final Exception e) {
            logger.error("Error communicating with notification microService "+e);
        }
    }

    private void notifyRH(LeaveRequest leaveRequest) {
        List<String> emails = collaboratorService.findRHEmail();
        for(String email : emails) {
            RestTemplate restTemplate = new RestTemplate();
            NotificationDTO request = new NotificationDTO();
            request.setCategory(NotificationCategory.LEAVE_REQUEST);
            request.setType(NotificationType.RELANCE);
            request.setValidatorEmail(email);
            request.setCollaboratorLastName(leaveRequest.getCollaborator().getLastName());
            request.setCollaboratorFirstName(leaveRequest.getCollaborator().getFirstName());
            try {
                restTemplate.postForObject(notificationConfig.getLeaveRequestsUrl(), request, ResponseEntity.class);
            } catch (final Exception e) {
                logger.error("Error communicating with notification microService "+e);
            }
        }
    }

}
