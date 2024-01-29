package com.groupehillstone.leavemgt.controllers;

import com.groupehillstone.leavemgt.dto.HolidayDTO;
import com.groupehillstone.leavemgt.entities.Holiday;
import com.groupehillstone.leavemgt.mapper.HolidayMapper;
import com.groupehillstone.leavemgt.services.HolidayService;
import com.groupehillstone.utils.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/enable/{id}")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ADMIN')")
    public ResponseEntity enableHoliday(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            holidayService.enableHoliday(id);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("ENABLED"));
        } catch (final Exception e) {
            logger.error("Error enabling holiday with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/disable/{id}")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ADMIN')")
    public ResponseEntity disableHoliday(@PathVariable("id") UUID id) {
        ResponseEntity response;
        try {
            holidayService.disableHoliday(id);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("DISABLED"));
        } catch (final Exception e) {
            logger.error("Error disabling holiday with id : "+id, e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/all/year")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
    public ResponseEntity getAllByYearPageable(@RequestParam int year,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "15") int size,
                                               @RequestParam(required = false) String keywords,
                                               @RequestParam(defaultValue = "created_at,desc") String[] sort) {
        ResponseEntity responseEntity;
        try {
            List<Sort.Order> orders = getOrders(sort);
            List<HolidayDTO> holidays;
            Pageable paging = PageRequest.of(page, size, Sort.by(orders));

            Page<Holiday> pageHolidays;
            if(StringUtils.isEmpty(keywords) ||StringUtils.isBlank(keywords)) {
                if(year == 0) {
                    pageHolidays = holidayService.findAll(paging);
                } else {
                    pageHolidays = holidayService.findAllByYear(year, paging);
                }
            } else {
                pageHolidays = holidayService.searchWithCriteria(keywords, year, paging);
            }

            holidays = holidayMapper.toDto(pageHolidays.getContent());
            Map<String, Object> response = new HashMap<>();
            response.put("holidays", holidays);
            response.put("currentPage", pageHolidays.getNumber());
            response.put("totalItems", pageHolidays.getTotalElements());
            response.put("totalPages", pageHolidays.getTotalPages());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (final Exception e) {
            logger.error("Error retrieving pageable holidays list for year : "+year, e);
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return responseEntity;
    }

    @GetMapping("/year/{year}")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_EMPLOYEE')")
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

    @GetMapping("/enabled")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN') or hasRole('ROLE_BUSINESS_UNIT_MANAGER') or hasRole('ROLE_TEAM_MANAGER') or hasRole('ROLE_BUSINESS') or hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity getAllByEnabled() {
        ResponseEntity response;
        try {
            final List<HolidayDTO> holidays = holidayMapper.toDto(holidayService.findAllByEnabled());
            response = ResponseEntity.status(HttpStatus.OK).body(holidays);
        } catch (final Exception e) {
            logger.error("Error retrieving holidays list : ", e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
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

    @GetMapping("/sync-holidays/year")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
    public ResponseEntity getAndInsertHolidays(@RequestParam(defaultValue = "metropole") String zone,
                                               @RequestParam(required = false) String year) {
        ResponseEntity response;
        try {
            holidayService.syncHolidaysByYear(zone, year);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("DONE"));
        } catch (final Exception e) {
            logger.error("Error getting and inserting holidays from API", e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/sync-holidays")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
    public ResponseEntity syncAllPossibleHolidays(@RequestParam(defaultValue = "metropole") String zone) {
        ResponseEntity response;
        try {
            holidayService.syncHolidays(zone);
            response = ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("DONE"));
        } catch (final Exception e) {
            logger.error("Error synchronization holidays dates: "+e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    @GetMapping("/holidays-years")
    @PreAuthorize("hasRole('ROLE_RH') or hasRole('ROLE_ADMIN')")
    public ResponseEntity getHolidaysYears() {
        ResponseEntity response;
        try {
            final List<Integer> yearsList = holidayService.findHolidaysYears();
            response = ResponseEntity.status(HttpStatus.OK).body(yearsList);
        } catch (final Exception e) {
            logger.error("Error retrieving holidays years list ",e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        return response;
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    private List<Sort.Order> getOrders(String[] sort) {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        if (sort[0].contains(",")) {
            // will sort more than 2 fields
            // sortOrder="field, direction"
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            // sort=[field, direction]
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }

}
