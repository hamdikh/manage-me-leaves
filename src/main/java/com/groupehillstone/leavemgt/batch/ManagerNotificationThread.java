package com.groupehillstone.leavemgt.batch;

import com.groupehillstone.leavemgt.dto.NotificationDTO;
import com.groupehillstone.leavemgt.entities.Collaborator;
import com.groupehillstone.leavemgt.entities.Leave;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.enums.NotificationCategory;
import com.groupehillstone.leavemgt.enums.NotificationType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ManagerNotificationThread implements Runnable {

    public void run() {}

    public ManagerNotificationThread(LeaveRequest leaveRequest, String url, NotificationType type) {
        if(NotificationType.NOTIFICATION.equals(type)) {
            notifyManager(leaveRequest, url);
        }
        if(NotificationType.CANCELLATION.equals(type)) {
            notifyCancellation(leaveRequest, url);
        }
    }

    private void notifyManager(LeaveRequest leaveRequest, String url) {
        RestTemplate restTemplate = new RestTemplate();
        NotificationDTO request = new NotificationDTO();
        request.setCategory(NotificationCategory.LEAVE_REQUEST);
        request.setType(NotificationType.NOTIFICATION);
        Collaborator collaborator = leaveRequest.getCollaborator();
        if(collaborator.getSalesManager() == null) {
            request.setValidatorEmail(leaveRequest.getCollaborator().getManager().getEmail());
        } else {
            request.setValidatorEmail(leaveRequest.getCollaborator().getSalesManager().getEmail());
        }
        request.setCollaboratorFirstName(collaborator.getFirstName());
        request.setCollaboratorLastName(collaborator.getLastName());
        restTemplate.postForObject(url, request, ResponseEntity.class);
    }

    private void notifyCancellation(LeaveRequest leaveRequest, String url) {
        RestTemplate restTemplate = new RestTemplate();
        NotificationDTO request = new NotificationDTO();
        request.setCategory(NotificationCategory.LEAVE_REQUEST);
        request.setType(NotificationType.CANCELLATION);
        Collaborator collaborator = leaveRequest.getCollaborator();
        if(collaborator.getSalesManager() == null) {
            request.setValidatorEmail(leaveRequest.getCollaborator().getManager().getEmail());
        } else {
            request.setValidatorEmail(leaveRequest.getCollaborator().getSalesManager().getEmail());
        }
        request.setCollaboratorFirstName(collaborator.getFirstName());
        request.setCollaboratorLastName(collaborator.getLastName());
        restTemplate.postForObject(url, request, ResponseEntity.class);
    }

}
