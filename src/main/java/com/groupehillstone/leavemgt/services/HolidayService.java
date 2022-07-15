package com.groupehillstone.leavemgt.services;

import com.groupehillstone.leavemgt.entities.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HolidayService {

    Page<Holiday> findAllByYear(int year, Pageable pageable);

    Page<Holiday> findAll(Pageable pageable);

    List<Holiday> searchWithCriteria(String keywords, int year);

    Holiday create(Holiday holiday);

    Holiday update(Holiday holiday);

    void delete(UUID id);

    void enableHoliday(UUID id);

    void disableHoliday(UUID id);

    List<Holiday> findAllByYear(String year);

    List<Holiday> findAllByYearAndEnabled(String year);

    List<Holiday> findAllByEnabled();

    Holiday findHolidayById(UUID id);

    List<LocalDate> enabledHolidaysDate();

    void syncHolidaysByYear(String zone, String year) throws IOException;

    void syncHolidays(String zone) throws IOException;

    List<Integer> findHolidaysYears();

}
