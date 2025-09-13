package com.test.booking.repository;

import com.test.booking.domain.Unit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUnitRepository extends CrudRepository<Unit, Long> {

}
