package com.groupehillstone.leavemgt.batch;

import com.groupehillstone.leavemgt.dto.NotificationDTO;
import com.groupehillstone.leavemgt.entities.LeaveRequest;
import com.groupehillstone.leavemgt.enums.NotificationCategory;
import com.groupehillstone.leavemgt.enums.NotificationType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class RhNotificationThread implements Runnable {

    public void run() {}

    public RhNotificationThread(LeaveRequest leaveRequest, String url, List<String> rhEmail) {
        notifyRH(leaveRequest, url, rhEmail);
    }

    private void notifyRH(LeaveRequest leaveRequest, String url, List<String> rhEmail) {
        for(String email : rhEmail) {
            RestTemplate restTemplate = new RestTemplate();
            NotificationDTO request = new NotificationDTO();
            request.setCategory(NotificationCategory.LEAVE_REQUEST);
            request.setType(NotificationType.NOTIFICATION);
            request.setValidatorEmail(email);
            request.setCollaboratorLastName(leaveRequest.getCollaborator().getLastName());
            request.setCollaboratorFirstName(leaveRequest.getCollaborator().getFirstName());
            restTemplate.postForObject(url, request, ResponseEntity.class);
        }
    }
}
