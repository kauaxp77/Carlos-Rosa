package com.carlosrosa.portfolio.controllers;

import com.carlosrosa.portfolio.entities.AuditLog;
import com.carlosrosa.portfolio.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        // Return latest logs first
        return ResponseEntity.ok(auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
    }
}
