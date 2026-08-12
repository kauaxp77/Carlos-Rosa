package com.carlosrosa.portfolio.repositories;

import com.carlosrosa.portfolio.entities.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteSettingsRepository extends JpaRepository<SiteSettings, String> {
}
