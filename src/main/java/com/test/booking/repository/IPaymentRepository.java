package com.test.booking.repository;

import com.test.booking.domain.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentRepository extends CrudRepository<Payment, Long> {
}
