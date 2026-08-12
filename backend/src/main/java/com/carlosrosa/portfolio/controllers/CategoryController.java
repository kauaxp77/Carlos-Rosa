package com.carlosrosa.portfolio.controllers;

import com.carlosrosa.portfolio.entities.Category;
import com.carlosrosa.portfolio.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public Category create(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
