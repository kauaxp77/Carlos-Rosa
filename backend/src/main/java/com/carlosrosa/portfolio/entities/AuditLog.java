package com.carlosrosa.portfolio.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // e.g. "CREATE", "DELETE"

    private String target; // e.g. "PORTFOLIO_ITEM", "USER"

    private String details;

    private String performedBy; // Username who did it

    @Column(updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog(String action, String target, String details, String performedBy) {
        this.action = action;
        this.target = target;
        this.details = details;
        this.performedBy = performedBy;
    }
}
