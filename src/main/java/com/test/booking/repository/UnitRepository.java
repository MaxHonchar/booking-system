package com.test.booking.repository;

import com.test.booking.domain.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UnitRepository extends ListPagingAndSortingRepository<Unit, Long>, CrudRepository<Unit, Long> {

    //TODO resolve with startDate and endDate
    @Query("""
        SELECT DISTINCT u
        FROM Unit u
        JOIN FETCH u.bookings b
        WHERE (:minCost IS NULL OR u.cost >= :minCost)
          AND (:maxCost IS NULL OR u.cost <= :maxCost)
          AND (:startDate IS NULL OR b.checkIn >= :startDate)
          AND (:endDate IS NULL OR b.checkOut <= :endDate)
        """)
    Page<Unit> findUnitsByFilters(
            @Param("minCost") Double minCost,
            @Param("maxCost") Double maxCost,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

}
