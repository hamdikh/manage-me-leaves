package com.groupehillstone.leavemgt.services.impl;

import com.groupehillstone.config.HolidaysConfig;
import com.groupehillstone.leavemgt.entities.Holiday;
import com.groupehillstone.leavemgt.repositories.HolidayRepository;
import com.groupehillstone.leavemgt.services.HolidayService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class HolidayServiceImpl implements HolidayService {

    private final Logger logger = LoggerFactory.getLogger(HolidayServiceImpl.class);

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private HolidaysConfig holidaysConfig;

    @Override
    public void getHolidaysFromAPI(String zone, String year) throws IOException {

        String currentYear = Integer.toString(LocalDate.now().getYear());
        if(StringUtils.isBlank(year) || StringUtils.isEmpty(year)) {
            year = currentYear;
        }
        if(StringUtils.isEmpty(zone) || StringUtils.isBlank(zone)) {
            zone = holidaysConfig.getZone();
        }
        URL holidaysListURL = new URL(holidaysConfig.getBaseURL().concat("/").concat(zone).concat("/"+year).concat(holidaysConfig.getExtension()));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(holidaysListURL.openStream()));

        String inputLine = in.readLine();
        Holiday holiday = new Holiday();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        JSONObject jo = new JSONObject(inputLine);
        for (Iterator<String> it = jo.keys(); it.hasNext(); ) {
            String key = it.next();
            holiday.setDate(LocalDate.parse(key, formatter));
            holiday.setDesignation((String) jo.get(key));
            holiday.setZone(zone);
            holiday.setYear(Integer.parseInt(year));
            holiday.setId(UUID.randomUUID());
            holidayRepository.save(holiday);
        }
    }

    @Override
    public Holiday create(Holiday holiday) {
        try {
            holiday = holidayRepository.save(holiday);
        } catch (final Exception e) {
            logger.error("Error creating holiday with designation : "+holiday.getDesignation(), e);
        }
        return holiday;
    }

    @Override
    public Holiday update(Holiday holiday) {
        try {
            holiday = holidayRepository.save(holiday);
        } catch (final Exception e) {
            logger.error("Error updating holiday with id : "+holiday.getId(), e);
        }
        return holiday;
    }

    @Override
    public void delete(UUID id) {
        try {
            if(holidayRepository.existsById(id)) {
                holidayRepository.delete(id);
            }
        } catch (final Exception e) {
            logger.error("Error deleting holiday with id : "+id, e);
        }
    }

    @Override
    public void enableHoliday(UUID id, String year) {
        try {
            if(holidayRepository.existsById(id)) {
                holidayRepository.enableHoliday(id, year);
            }
        } catch (final Exception e) {
            logger.error("Error enabling holiday with id : "+id, e);
        }
    }

    @Override
    public void disableHoliday(UUID id, String year) {
        try {
            if(holidayRepository.existsById(id)) {
                holidayRepository.disableHoliday(id, year);
            }
        } catch (final Exception e) {
            logger.error("Error disbaling holiday with id : "+id, e);
        }
    }

    @Override
    public List<Holiday> findAllByYear(String year) {
        List<Holiday> holidays = null;
        try {
            holidays = holidayRepository.findAllByYear(year);
        } catch (final Exception e) {
            logger.error("Error retrieving holidays list for year : "+year, e);
        }
        return holidays;
    }

    @Override
    public List<Holiday> findAllByYearAndEnabled(String year) {
        List<Holiday> holidays = null;
        try {
            holidays = holidayRepository.findAllByYearAndEnabled(Integer.parseInt(year));
        } catch (final Exception e) {
            logger.error("Error retrieving enabled holidays list for year : "+year, e);
        }
        return holidays;
    }

    @Override
    public Holiday findHolidayById(UUID id) {
        Holiday holiday = null;
        try {
            holiday = holidayRepository.findHolidayById(id);
        } catch (final Exception e) {
            logger.error("Error retrieving holiday by id : "+id, e);
        }
        return holiday;
    }

    @Override
    public List<LocalDate> enabledHolidaysDate() {
        List<LocalDate> holidaysList = null;
        try {
            holidaysList = holidayRepository.enabledHolidaysDate();
        } catch (final Exception e) {
            logger.error("Error retrieving holidays enabled list", e);
        }
        return holidaysList;
    }
}
