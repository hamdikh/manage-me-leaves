package com.groupehillstone.leavemgt.controllers;

import com.groupehillstone.leavemgt.dto.HolidayDTO;
import com.groupehillstone.leavemgt.mapper.HolidayMapper;
import com.groupehillstone.leavemgt.services.HolidayService;
import com.groupehillstone.utils.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/holidays")
@Slf4j
public class HolidayController {

    private final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private HolidayMapper holidayMapper;

    @GetMapping("/enable/{id}/{year}")
    public ResponseEntity enableHoliday(@PathVariable("id") UUID id, @PathVariable("year") String year) {
        ResponseEntity response;
        try {
            holidayService.enableHoliday(id, year);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("ENABLED"));
        } catch (final Exception e) {
            logger.error("Error enabling holiday with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/disable/{id}/{year}")
    public ResponseEntity disableHoliday(@PathVariable("id") UUID id, @PathVariable("year") String year) {
        ResponseEntity response;
        try {
            holidayService.disableHoliday(id, year);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("DISABLED"));
        } catch (final Exception e) {
            logger.error("Error disabling holiday with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/year/{year}")
    public ResponseEntity getAllByYear(@PathVariable("year") String year) {
        ResponseEntity response;
        try {
            final List<HolidayDTO> holidays = holidayMapper.toDto(holidayService.findAllByYear(year));
            response = ResponseEntity.status(HttpStatus.OK).body(holidays);
        } catch (final Exception e) {
            logger.error("Error retrieving holidays list by year : "+year, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/enabled/year/{year}")
    public ResponseEntity getAllByYearAndEnabled(@PathVariable("year") String year) {
        ResponseEntity response;
        try {
            final List<HolidayDTO> holidays = holidayMapper.toDto(holidayService.findAllByYearAndEnabled(year));
            response = ResponseEntity.status(HttpStatus.OK).body(holidays);
        } catch (final Exception e) {
            logger.error("Error retrieving holidays list by year : "+year, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            final HolidayDTO holiday = holidayMapper.toDto(holidayService.findHolidayById(id));
            response = ResponseEntity.status(HttpStatus.OK).body(holiday);
        } catch (final Exception e) {
            logger.error("Error retrieving holiday by id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/insert-from-api")
    public ResponseEntity getAndInsertHolidays(@RequestParam(defaultValue = "metropole") String zone,
                                               @RequestParam(required = false) String year) {
        ResponseEntity response;
        try {
            holidayService.getHolidaysFromAPI(zone, year);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("DONE"));
        } catch (final Exception e) {
            logger.error("Error getting and inserting holidays from API", e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }


}
