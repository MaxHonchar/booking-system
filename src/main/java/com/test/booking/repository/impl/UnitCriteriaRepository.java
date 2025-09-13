package com.test.booking.repository.impl;

import com.test.booking.domain.Booking;
import com.test.booking.domain.Event;
import com.test.booking.domain.Unit;
import com.test.booking.domain.UnitProperties;
import com.test.booking.dtos.UnitSearchDto;
import com.test.booking.repository.IUnitCriteriaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UnitCriteriaRepository implements IUnitCriteriaRepository {

    private static final String BOOKINGS = "bookings";
    private static final String EVENTS = "events";
    private static final String PROPERTIES = "properties";
    private static final String TYPE = "type";
    private static final String CHECK_IN = "checkIn";
    private static final String CHECK_OUT = "checkOut";
    private static final String COST = "cost";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Unit> findUnitsByFilters(UnitSearchDto unitSearchDto, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        List<Unit> units = getUnits(unitSearchDto, pageable, builder);
        Long total = getTotalUnits(unitSearchDto, builder);
        return new PageImpl<>(units, pageable, total);
    }

    private Long getTotalUnits(UnitSearchDto unitSearchDto, CriteriaBuilder builder) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Unit> countRoot = query.from(Unit.class);
        Predicate predicate = buildPredicate(builder, countRoot, unitSearchDto);
        query.select(builder.countDistinct(countRoot)).where(predicate);
        return entityManager.createQuery(query).getSingleResult();
    }

    private List<Unit> getUnits(UnitSearchDto unitSearchDto, Pageable pageable, CriteriaBuilder builder) {
        CriteriaQuery<Unit> query = builder.createQuery(Unit.class);
        Root<Unit> root = query.from(Unit.class);

        Predicate predicate = buildPredicate(builder, root, unitSearchDto);

        query.select(root).distinct(true);
        query.where(predicate);
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));

        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private Predicate buildPredicate(CriteriaBuilder builder, Root<Unit> root, UnitSearchDto searchDto) {

        List<Predicate> predicates = new ArrayList<>();

        Join<Unit, Event> events = root.join(EVENTS, JoinType.INNER);
        Join<Unit, UnitProperties> properties = root.join(PROPERTIES, JoinType.INNER);
        Join<Unit, Booking> bookings = root.join(BOOKINGS, JoinType.LEFT);

        Optional.ofNullable(searchDto.getAccommodationType()).ifPresent(accommodationType -> {
            predicates.add(builder.equal(properties.get(TYPE), accommodationType));
        });

        Optional.ofNullable(searchDto.getEventType()).ifPresent(eventType -> {
            predicates.add(builder.notEqual(events.get(TYPE), eventType));
        });

        Optional.ofNullable(searchDto.getFromDate()).ifPresent(fromDate -> {
            predicates.add(builder.or(builder.isNull(bookings.get(CHECK_IN)),
                    builder.lessThanOrEqualTo(bookings.get(CHECK_IN), fromDate)));
        });

        Optional.ofNullable(searchDto.getToDate()).ifPresent(toDate -> {
            predicates.add(builder.or(builder.isNull(bookings.get(CHECK_OUT)),
                    builder.lessThanOrEqualTo(bookings.get(CHECK_OUT), toDate)));
        });

        Optional.ofNullable(searchDto.getMinCost()).ifPresent(cost -> {
            predicates.add(builder.greaterThanOrEqualTo(root.get(COST), cost));
        });

        Optional.ofNullable(searchDto.getMaxCost()).ifPresent(cost -> {
            predicates.add(builder.lessThanOrEqualTo(root.get(COST), cost));
        });

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
