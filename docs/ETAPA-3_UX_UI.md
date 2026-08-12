# ETAPA 3 — UX/UI & Design System

## 1. Mapeamento da Experiência (User Flow)
**Visitante:**
1. Acessa `/`
2. Vê a "Hero Avançada" (Nome "Carlos Rosa" sobre vídeo/imagem de impacto, tipografia *Cinzel*).
3. Navega intuitivamente para Portfólio (grids fluidas que reagem revelando conteúdo suavemente).
4. Acesa Ao Vivo (área de "Realtime"). Lightbox interativo se expande nas fotos recentes sem quebrar a rota de URL atual (pode usar state parameters/View Transitions API).
5. Formulário de Contato de alto contraste ao fim da navegação.

**Administrador:**
1. Acessa `/admin`.
2. Recebe formulário "Mistério" Dark (Autenticação MFA/JWT Auth Rate Limited).
3. Redirecionado para `/admin/dashboard`.
4. Visão rápida (médias cadastradas, eventos ativos, visualizações).
5. Abre Media Library (Upload em lote protegido), cria novos projetos (Rascunho/Publicado), controla links do Ao Vivo.

## 2. Sitemap
```text
(Público) 
/
  ├─ /#sobre
  ├─ /#portfolio
  ├─ /#aovivo
  └─ /#contato

(Administrativo - Protegido por JWT)
/admin/login
/admin/dashboard
/admin/portfolio (CRUD)
/admin/media-library (Uploads, S3 manager)
/admin/live (Real-time Manager, CRUD Eventos)
/admin/settings (Contatos, Tokens, Auditoria)
```

## 3. Design System Tokens (Global CSS)
Este Design System já reflete os direcionamentos de "Luxo, Cinema, Profundidade, Minimalism" sem uso pesado de frameworks.

```css
:root {
    /* Colors - Black Focus */
    --color-black: #080808;
    --color-black-soft: #101010;
    --color-charcoal: #141414;
    --color-card: #1B1B1B;
    --color-white: #F5F5F5;
    --color-white-pure: #FFFFFF;
    
    /* Grays & Muted */
    --color-muted: #A3A3A3;
    --color-muted-dark: #6F6F6F;
    --color-border: #292929;

    /* Accents - Gold Cinematic */
    --color-gold: #D4AF37;
    --color-gold-soft: #B8963E;

    /* Semantic */
    --color-error: #D04F4F;
    --color-success: #4FD08B;
    --color-focus: rgba(212, 175, 55, 0.4);

    /* Fonts */
    --font-display: 'Cinzel', serif;
    --font-ui: 'Inter', sans-serif;

    /* Spacing Scale */
    --space-xs: .5rem;    /* 8px */
    --space-sm: .75rem;   /* 12px */
    --space-md: 1rem;     /* 16px */
    --space-lg: 2rem;     /* 32px */
    --space-xl: 4rem;     /* 64px */
    --space-2xl: 8rem;    /* 128px */

    /* Radii */
    --radius-sm: 6px;
    --radius-md: 10px;
    --radius-lg: 18px;

    /* Motion */
    --ease-cinematic: cubic-bezier(.16, .8, .24, 1);
    --duration-fast: 0.25s;
    --duration-slow: 0.9s;

    /* Layout & Containers */
    --container-max: 1440px;
    --header-height: 80px;
}
```

## 4. Componentes Chave da Interface
* **Botão Primário:** Fundo Transparente, borda sutil `:hover { border-color: var(--color-gold) }`.
* **Lightbox (Fallback 2D):** Imagens de altíssima fidelidade sobrepostas a um fundo sólido preto absoluto 95% opacity. Scrollbar congelada, prioridade de teclado ativada para acessibilidade ESC.
* **Carrosséis (Ao Vivo):** Scroll snap sem customizações exorbitantes em JS (apenas scroll fluido via CSS e observer para lazy/loading).
* **Hover Reveal:** Imagens com desaturação base (grayscale(20%) saturate(80%)), ganhando saturação pura no mouse over, transmitindo um efeito de revelação ("filme sendo processado"). 

## 5. View Transitions API / Estado Transicional
Conforme solicitado, não há uma SPA complexa sem necessidade no site final, usufruindo primariamente do scroll vertical suave e interseções para revelar, exceto para abrir projetos únicos se a arquitetura necessitar; porém para este portfólio âncoras locais mantêm a taxa de interação alta com complexidade baixa.
