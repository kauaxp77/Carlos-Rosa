# ETAPA 8 — Documentação de Segurança e Auditoria

## 1. Threat Model & OWASP Mitigation
Em conformidade com a visão do produto e as diretrizes do OWASP Top 10, a arquitetura da Plataforma Carlos Rosa protege contra:

1. **Acesso Quebrado (Broken Access Control):** 
   - Backend reforçado pelo Spring Security com validadores baseados em papéis (`ROLE_ADMIN`, `ROLE_EDITOR`).
   - Todos os controllers de administração (ex: `PUT /api/v1/portfolio/`) exigem anotação de segurança `@PreAuthorize("hasAnyRole('ADMIN','EDITOR')")`.

2. **Criptografia e Segredos:**
   - Senhas criptografadas usando algoritmo seguro no ato do registro (No caso do Spring: `BCryptPasswordEncoder`).
   - Variáveis sensíveis (`JWT_SECRET`, `AWS_ACCESS_KEY`) jamais commitadas (mantidas via Docker Secrets/Variaveis ENV protegidas).

3. **Injeção (Injection) & CSRF:**
   - Queries nativas proibidas via concatenação. O ORM Hibernate previne inerentemente injeções SQL através do uso de prepared statements (JPA).
   - O uso de JWT no Header `Authorization: Bearer <token>` isola naturalmente o frontend de CSRF (cross-site request forgery) não baseada em Session Cookies.

## 2. Segurança no Fluxo de Upload de Arquivos (Crítico)
* **Pre-signed URLs da AWS:** Para arquivos gigantes de portfólios, não fazemos o passe pelo Backend da aplicação (pois isso esgota memória da JVM / rede limit). Em vez disso: O front-end autenticado solicita uma URI curta ao Backend. O Backend negocia um PUT pre-signed com o S3. O Client envia direto ao Bucket S3, isolado do nosso cluster.
* **Validação Assíncrona do MIME Type:** Assim que o upload acaba, uma AWS Lambda (ou worker local) aciona a verificação mágica do byte no cabeçalho do arquivo, validando o container de imagem, banindo executáveis e procedendo para criar Thumbnails.

## 3. Segurança do Cabeçalho e NGINX (CSP)
O container de Nginx servirá os arquivos estáticos injetando sempre proteção de Headers (também já configurado temporariamente no index.html anterior):

```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-Frame-Options "DENY" always;
add_header Permissions-Policy "geolocation=(), microphone=(), camera=()" always;
add_header Content-Security-Policy "default-src 'self' 'unsafe-inline' https:; img-src 'self' data: https:;" always;
```

## 4. Auditoria (`audit_logs`)
Todas as ações geram uma entrada rastreável no banco via entidade `AuditLog`. Sempre capturando `USER_ID`, `OPERATION` e, quando legalmente amparado ao administrador local, o escopo da requisição. Nenhuma PII (informação identificável confidencial como senhas e tokens reais) deve transitar nem prosseguir aos logs.
