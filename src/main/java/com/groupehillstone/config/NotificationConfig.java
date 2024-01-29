package com.groupehillstone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "manageme.notifications")
public class NotificationConfig {

    private String leaveRequestsUrl;

    public String getLeaveRequestsUrl() {
        return leaveRequestsUrl;
    }

    public void setLeaveRequestsUrl(String leaveRequestsUrl) {
        this.leaveRequestsUrl = leaveRequestsUrl;
    }
}
