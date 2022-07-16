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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private HolidaysConfig holidaysConfig;

    @Override
    public void syncHolidays(String zone) throws IOException {

        if(StringUtils.isEmpty(zone) || StringUtils.isBlank(zone)) {
            zone = holidaysConfig.getZone();
        }
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

    @Override
    public void syncHolidaysByYear(String zone, String year) throws IOException {

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
            if(!holidayRepository.existsByDate(LocalDate.parse(key, formatter))) {
                holiday.setDate(LocalDate.parse(key, formatter));
                holiday.setDesignation((String) jo.get(key));
                holiday.setZone(zone);
                holiday.setId(UUID.randomUUID());
                holidayRepository.save(holiday);
            }
        }
    }

    @Override
    public Page<Holiday> searchWithCriteria(String keywords, int year, Pageable paging) {
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder queryBuilderCount = new StringBuilder();
        StringBuilder init = new StringBuilder("SELECT DISTINCT(h.*) FROM public.holidays AS h");
        StringBuilder count = new StringBuilder("SELECT DISTINCT(COUNT(h.id)) FROM public.holidays AS h");
        StringBuilder condition = new StringBuilder(" WHERE h.is_deleted = 'false'");
        StringBuilder order = new StringBuilder(" ORDER BY h.date ASC");

        if(StringUtils.isNotEmpty(keywords) && StringUtils.isNotBlank(keywords)) {
            condition.append(" AND (LOWER(h.designation) LIKE '%"+keywords+"%' OR concat('0',cast(EXTRACT(day FROM date) as text)) LIKE '%"+keywords+
                    "%' OR concat('0',cast(EXTRACT(month FROM date) as text)) LIKE '%"+keywords+"%' OR cast(EXTRACT(year FROM date) as text) LIKE '%"+keywords+"%')");
        }
        if(year != 0) {
            condition.append(" AND EXTRACT(year FROM date) = '"+year+"'");
        }

        queryBuilder.append(init).append(condition).append(order);

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), Holiday.class);
        query.setFirstResult(paging.getPageNumber() * paging.getPageSize());
        query.setMaxResults(paging.getPageSize());
        final List<Holiday> holidays = query.getResultList();

        queryBuilderCount.append(count).append(condition);
        Query countQuery = entityManager.createNativeQuery(queryBuilderCount.toString());
        long countResult = Long.parseLong(countQuery.getSingleResult().toString());

        Page holidaysPage = new PageImpl(holidays, paging, countResult);

        return holidaysPage;
    }

    @Override
    public Page<Holiday> findAllByYear(int year, Pageable pageable) {
        Page<Holiday> holidays = null;
        try {
            holidays = holidayRepository.findAllByYear(year, pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving pageable holidays list for year: "+year, e);
        }
        return holidays;
    }

    @Override
    public Page<Holiday> findAll(Pageable pageable) {
        Page<Holiday> holidays = null;
        try {
            holidays = holidayRepository.findAll(pageable);
        } catch (final Exception e) {
            logger.error("Error retrieving pageable holidays list ",e);
        }
        return holidays;
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
    public void enableHoliday(UUID id) {
        try {
            if(holidayRepository.existsById(id)) {
                holidayRepository.enableHoliday(id);
            }
        } catch (final Exception e) {
            logger.error("Error enabling holiday with id : "+id, e);
        }
    }

    @Override
    public void disableHoliday(UUID id) {
        try {
            if(holidayRepository.existsById(id)) {
                holidayRepository.disableHoliday(id);
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
    public List<Holiday> findAllByEnabled() {
        List<Holiday> holidays = null;
        try {
            holidays = holidayRepository.findAllByEnabled();
        } catch (final Exception e) {
            logger.error("Error retrieving enabled holidays list : ", e);
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

    @Override
    public List<Integer> findHolidaysYears() {
        List<Integer> yearsList = null;
        try {
            yearsList = holidayRepository.findHolidaysYears();
        } catch (final Exception e) {
            logger.error("Error retrieving holidays years list ",e);
        }
        return yearsList;
    }
}
