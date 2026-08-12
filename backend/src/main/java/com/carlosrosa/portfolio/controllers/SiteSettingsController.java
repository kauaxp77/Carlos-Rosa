package com.carlosrosa.portfolio.controllers;

import com.carlosrosa.portfolio.entities.SiteSettings;
import com.carlosrosa.portfolio.repositories.SiteSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/settings")
@CrossOrigin(origins = "*")
public class SiteSettingsController {

    @Autowired
    private SiteSettingsRepository settingsRepository;

    @GetMapping
    public List<SiteSettings> getAll() {
        return settingsRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SiteSettings update(@RequestBody SiteSettings setting) {
        return settingsRepository.save(setting);
    }
}
