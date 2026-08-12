package com.carlosrosa.portfolio.repositories;

import com.carlosrosa.portfolio.entities.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
}
