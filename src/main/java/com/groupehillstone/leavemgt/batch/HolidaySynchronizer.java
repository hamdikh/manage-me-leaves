package com.groupehillstone.leavemgt.batch;

import com.groupehillstone.ManageMeLeavesApplication;
import com.groupehillstone.config.HolidaysConfig;
import com.groupehillstone.leavemgt.entities.Holiday;
import com.groupehillstone.leavemgt.repositories.HolidayRepository;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.UUID;

@Component
@EnableScheduling
public class HolidaySynchronizer {

    private final Logger logger = LoggerFactory.getLogger(HolidaySynchronizer.class);

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private HolidaysConfig holidaysConfig;

    public static void main(String[] args) {
        SpringApplication.run(HolidaySynchronizer.class, args);
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void holidaysSync() throws IOException {

        String zone = "metropole";
        URL holidaysListURL = new URL(holidaysConfig.getBaseURL().concat("/").concat(zone).concat(holidaysConfig.getExtension()));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(holidaysListURL.openStream()));

        String inputLine = in.readLine();
        Holiday holiday = new Holiday();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        JSONObject jo = new JSONObject(inputLine);
        for (Iterator<String> it = jo.keys(); it.hasNext(); ) {
            String key = it.next();
            if(!holidayRepository.existsByDate(LocalDate.parse(key, formatter))) {
                holiday.setDate(LocalDate.parse(key, formatter));
                holiday.setDesignation((String) jo.get(key));
                holiday.setZone(zone);
                holiday.setId(UUID.randomUUID());
                holidayRepository.save(holiday);
            }
        }
    }

}
