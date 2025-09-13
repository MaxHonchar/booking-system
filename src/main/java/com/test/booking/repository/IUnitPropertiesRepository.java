package com.test.booking.repository;

import com.test.booking.domain.UnitProperties;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUnitPropertiesRepository extends CrudRepository<UnitProperties, Long> {
}
