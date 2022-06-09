package com.groupehillstone.leavemgt.services;

import com.groupehillstone.leavemgt.entities.Holiday;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HolidayService {

    Holiday create(Holiday holiday);

    Holiday update(Holiday holiday);

    void delete(UUID id);

    void enableHoliday(UUID id, String year);

    void disableHoliday(UUID id, String year);

    List<Holiday> findAllByYear(String year);

    List<Holiday> findAllByYearAndEnabled(String year);

    Holiday findHolidayById(UUID id);

    List<LocalDate> enabledHolidaysDate();

    void getHolidaysFromAPI(String zone, String year) throws IOException;

}
