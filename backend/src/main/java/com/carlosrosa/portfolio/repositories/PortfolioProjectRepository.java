package com.carlosrosa.portfolio.repositories;

import com.carlosrosa.portfolio.entities.PortfolioProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, Long> {

    // Custom query to fetch only published projects for the public API
    List<PortfolioProject> findByStatusOrderBySortOrderAsc(String status);

    Optional<PortfolioProject> findBySlug(String slug);
}
