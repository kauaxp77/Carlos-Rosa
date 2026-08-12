package com.carlosrosa.portfolio.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class SiteSettings {
    @Id
    private String keyName;
    private String value;
}
