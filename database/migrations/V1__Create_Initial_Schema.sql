-- SQL Migration: V1__Create_Initial_Schema.sql
-- Framework: Flyway
-- Description: Mapeamento completo do Banco de Dados Relacional, tabelas de controle de acesso (RBAC), portfólio, e auditoria, totalmente desenhado com constraint foreign keys e regras anti-exclusão forçadas.

-- ==========================================
-- 1. AUTH & RBAC (Role-Based Access Control)
-- ==========================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    mfa_secret VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- ADMIN, EDITOR, VIEWER
    description VARCHAR(255)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY(user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


-- ==========================================
-- 2. PORTFÓLIO & CATEGORIAS
-- ==========================================
CREATE TABLE portfolio_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE portfolio_projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    date_event DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- DRAFT, PUBLISHED, ARCHIVED
    cover_media_id BIGINT NULL, -- FK referenciando a capa, adicionada depois
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL
);

CREATE TABLE portfolio_project_categories (
    project_id BIGINT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY(project_id, category_id),
    CONSTRAINT fk_ppc_project FOREIGN KEY (project_id) REFERENCES portfolio_projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_ppc_category FOREIGN KEY (category_id) REFERENCES portfolio_categories(id) ON DELETE CASCADE
);

-- ==========================================
-- 3. GESTÃO DE MÍDIA (Armazenadas externamente no S3)
-- ==========================================
CREATE TABLE media_library (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    storage_key VARCHAR(500) NOT NULL UNIQUE, -- Objeto referencial AWS S3, ex: originals/uuid_file.jpg
    media_type VARCHAR(20) NOT NULL, -- IMAGE, VIDEO, DOCUMENT
    alt_text VARCHAR(255),
    uploader_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_ml_uploader FOREIGN KEY (uploader_id) REFERENCES users(id)
);

-- Relacionamento M:N para Mídia X Projeto (Múltiplas Fotos/Vídeos num Portfólio)
CREATE TABLE portfolio_project_media (
    project_id BIGINT NOT NULL,
    media_id BIGINT NOT NULL,
    sort_order INT DEFAULT 0,
    PRIMARY KEY(project_id, media_id),
    CONSTRAINT fk_ppm_project FOREIGN KEY (project_id) REFERENCES portfolio_projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_ppm_media FOREIGN KEY (media_id) REFERENCES media_library(id) ON DELETE CASCADE
);

-- Fix the FK na capa do projeto agora que media_library existe
ALTER TABLE portfolio_projects ADD CONSTRAINT fk_pp_cover_media FOREIGN KEY (cover_media_id) REFERENCES media_library(id) ON DELETE SET NULL;


-- ==========================================
-- 4. EXPERIÊNCIA "AO VIVO" (Eventos Realtime)
-- ==========================================
CREATE TABLE live_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, CLOSED, ARCHIVED
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE live_event_media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    media_id BIGINT NOT NULL,
    post_text VARCHAR(500),
    posted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lem_event FOREIGN KEY (event_id) REFERENCES live_events(id) ON DELETE CASCADE,
    CONSTRAINT fk_lem_media FOREIGN KEY (media_id) REFERENCES media_library(id) ON DELETE CASCADE
);

-- ==========================================
-- 5. DEPOIMENTOS
-- ==========================================
CREATE TABLE testimonials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(150) NOT NULL,
    event_context VARCHAR(150),
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 6. LOGS & AUDITORIA (Observabilidade)
-- ==========================================
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action_type VARCHAR(50) NOT NULL, -- LOGIN, UPLOAD_MEDIA, DELETE_MEDIA, UPDATE_PROJECT...
    resource_name VARCHAR(100),
    resource_id BIGINT,
    ip_address VARCHAR(45),
    outcome VARCHAR(20), -- SUCCESS, FAILED
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- INSERÇÕES BÁSICAS PARA BOOTSTRAP (Exemplo de Dados Default de Role)
INSERT INTO roles (name, description) VALUES ('ADMIN', 'Administrador Total');
INSERT INTO roles (name, description) VALUES ('EDITOR', 'Gerenciador de Portfólio e Mídias');
INSERT INTO roles (name, description) VALUES ('VIEWER', 'Somente visualização de dados do Content');
