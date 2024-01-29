package com.groupehillstone.leavemgt.repositories;

import com.groupehillstone.leavemgt.entities.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {

    @Query(value = "SELECT h.* FROM public.holidays AS h WHERE h.is_deleted = false AND EXTRACT(year FROM date) = :year ORDER BY h.date ASC", nativeQuery = true)
    Page<Holiday> findAllByYear(int year, Pageable pageable);

    @Query(value = "SELECT h.* FROM public.holidays AS h WHERE h.is_deleted = false ORDER BY h.date ASC", nativeQuery = true)
    Page<Holiday> findAll(Pageable pageable);

    @Query(value = "SELECT DISTINCT(h.*) FROM public.holidays AS h WHERE is_deleted = 'false' AND EXTRACT(year FROM date) = :year", nativeQuery = true)
    List<Holiday> findAllByYear(String year);

    @Query(value = "SELECT DISTINCT(h.*) FROM public.holidays AS h WHERE is_deleted = 'false' AND is_enabled = 'true' AND EXTRACT(year FROM date) = :year", nativeQuery = true)
    List<Holiday> findAllByYearAndEnabled(Integer year);

    @Query(value = "SELECT DISTINCT(h.*) FROM public.holidays AS h WHERE is_deleted = 'false' AND is_enabled = 'true'", nativeQuery = true)
    List<Holiday> findAllByEnabled();

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.holidays SET is_enabled = 'false' WHERE id = :id", nativeQuery = true)
    void disableHoliday(UUID id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.holidays SET is_enabled = 'true' WHERE id = :id", nativeQuery = true)
    void enableHoliday(UUID id);

    @Transactional
    @Modifying
    @Query("UPDATE Holiday h SET h.isDeleted = true WHERE h.id = :id")
    void delete(UUID id);

    @Query("SELECT h FROM Holiday h WHERE h.isDeleted = false AND h.id = :id")
    Holiday findHolidayById(UUID id);

    @Query("SELECT h.date FROM Holiday h WHERE h.isDeleted = false AND h.isEnabled = true")
    List<LocalDate> enabledHolidaysDate();

    boolean existsByDate(LocalDate date);

    @Query(value = "SELECT DISTINCT(EXTRACT(year FROM date)) FROM public.holidays AS h WHERE h.is_deleted = false ORDER BY EXTRACT(year FROM date) DESC", nativeQuery = true)
    List<Integer> findHolidaysYears();

}
