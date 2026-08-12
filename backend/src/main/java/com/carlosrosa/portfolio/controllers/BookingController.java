package com.carlosrosa.portfolio.controllers;

import com.carlosrosa.portfolio.entities.BookingRequest;
import com.carlosrosa.portfolio.repositories.BookingRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRequestRepository bookingRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public List<BookingRequest> getAll() {
        return bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @PostMapping
    public BookingRequest create(@RequestBody BookingRequest request) {
        return bookingRepository.save(request); // Publicly accessible to submit quotes
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingRequest updateStatus(@PathVariable Long id, @RequestParam String status) {
        BookingRequest b = bookingRepository.findById(id).orElseThrow();
        b.setStatus(status);
        return bookingRepository.save(b);
    }
}
