package com.groupehillstone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "holidays.url")
public class HolidaysConfig {

    private String baseURL;

    private String zone;

    private String year;

    private String extension;

    public String getBaseURL() {
        return baseURL;
    }

    public String getZone() {
        return zone;
    }

    public String getYear() {
        return year;
    }

    public String getExtension() {
        return extension;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
