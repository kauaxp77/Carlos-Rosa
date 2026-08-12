package com.carlosrosa.portfolio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_projects")
@Getter
@Setter
@NoArgsConstructor
public class PortfolioProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_event")
    private LocalDate eventDate;

    @Column(nullable = false)
    private String status = "PUBLISHED"; // DRAFT, PUBLISHED, ARCHIVED

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // Temporary field assuming media URL is stored directly for MVP functional test
    // In full production, this would join the media_library table as created in
    // Flyway
    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
