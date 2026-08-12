package com.carlosrosa.portfolio.controllers;

import com.carlosrosa.portfolio.entities.PortfolioProject;
import com.carlosrosa.portfolio.repositories.PortfolioProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PortfolioController {

    @Autowired
    PortfolioProjectRepository portfolioRepository;

    // PUBLIC ENDPOINT: Any user can fetch published projects
    @GetMapping
    public ResponseEntity<List<PortfolioProject>> getPublishedProjects() {
        List<PortfolioProject> projects = portfolioRepository.findByStatusOrderBySortOrderAsc("PUBLISHED");
        return ResponseEntity.ok(projects);
    }

    // PUBLIC ENDPOINT: Get project by slug
    @GetMapping("/{slug}")
    public ResponseEntity<PortfolioProject> getProjectBySlug(@PathVariable String slug) {
        return portfolioRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PROTECTED ENDPOINT: Only Admins or Editors can create new projects
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<PortfolioProject> createProject(@RequestBody PortfolioProject project) {
        PortfolioProject savedProject = portfolioRepository.save(project);
        return ResponseEntity.ok(savedProject);
    }
}
