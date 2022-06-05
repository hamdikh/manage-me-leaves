package com.groupehillstone.leavemgt.repositories;

import com.groupehillstone.leavemgt.entities.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {

    @Query(value = "SELECT DISTINCT(h.*) FROM public.holidays AS h WHERE is_deleted = 'false' AND EXTRACT(year FROM date) = :year", nativeQuery = true)
    List<Holiday> findAllByYear(String year);

    @Query(value = "SELECT DISTINCT(h.*) FROM public.holidays AS h WHERE is_deleted = 'false' AND is_enabled = 'true' AND EXTRACT(year FROM date) = :year", nativeQuery = true)
    List<Holiday> findAllByYearAndEnabled(String year);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.holidays SET is_enabled = 'false' WHERE id = :id AND EXTRACT(year FROM date) = :year", nativeQuery = true)
    void disableHoliday(UUID id, String year);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.holidays SET is_enabled = 'true' WHERE id = :id AND EXTRACT(year FROM date) = :year", nativeQuery = true)
    void enableHoliday(UUID id, String year);

    @Transactional
    @Modifying
    @Query("UPDATE Holiday h SET h.isDeleted = true WHERE h.id = :id")
    void delete(UUID id);

}
