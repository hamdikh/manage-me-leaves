package com.groupehillstone.leavemgt.batch;

import com.groupehillstone.leavemgt.dto.NotificationDTO;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.enums.LeaveStatus;
import com.groupehillstone.leavemgt.enums.NotificationCategory;
import com.groupehillstone.leavemgt.enums.NotificationType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CollaboratorNotificationThread implements Runnable {

    public void run() {}

    public CollaboratorNotificationThread(LeaveRequest leaveRequest, String url) {
        notifyCollaborator(leaveRequest, url);
    }

    public void notifyCollaborator(LeaveRequest leaveRequest, String url) {
        RestTemplate restTemplate = new RestTemplate();
        NotificationDTO request = new NotificationDTO();
        request.setCategory(NotificationCategory.LEAVE_REQUEST);
        if(LeaveStatus.PENDING.equals(leaveRequest.getStatus())) {
            request.setType(NotificationType.SAVED);
        } else if(LeaveStatus.VALIDATED.equals(leaveRequest.getStatus())) {
            request.setType(NotificationType.VALIDATED);
        } else if(LeaveStatus.REJECTED.equals(leaveRequest.getStatus())) {
            request.setType(NotificationType.REJECTED);
        }
        request.setCollaboratorEmail(leaveRequest.getCollaborator().getEmail());
        request.setCollaboratorFirstName(leaveRequest.getCollaborator().getFirstName());
        restTemplate.postForObject(url, request, ResponseEntity.class);
    }

}
