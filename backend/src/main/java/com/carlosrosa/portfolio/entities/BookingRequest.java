package com.carlosrosa.portfolio.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class BookingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String eventType;
    private String eventDate;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String status = "PENDING"; // PENDING, RESPONDED, CLOSED

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
