# 🎨 Guide de Design Evalia - Couleurs Pastel

Ce guide vous aide à utiliser le nouveau système de design avec couleurs pastel et animations fluides.

## 📋 Table des matières
- [Palette de couleurs](#palette-de-couleurs)
- [Boutons](#boutons)
- [Cartes](#cartes)
- [Badges](#badges)
- [Animations](#animations)
- [Tables](#tables)
- [Navigation](#navigation)

---

## 🎨 Palette de couleurs

### Couleurs principales - Bleu Pastel
```css
--pastel-blue-100: #D1E9F1  /* Très clair */
--pastel-blue-400: #47A7C7  /* Moyen */
--pastel-blue-600: #256F86  /* Foncé */
```

### Couleurs secondaires
```css
--pastel-pink: #FFD6E8
--pastel-lavender: #E6E6FA
--pastel-mint: #D5F5E3
--pastel-peach: #FFDAB9
--pastel-coral: #FFCCCB
```

### Dégradés
```css
--gradient-blue    /* Bleu pastel */
--gradient-pink    /* Rose → Lavande */
--gradient-mint    /* Menthe → Bleu */
--gradient-peach   /* Pêche → Corail */
```

---

## 🔘 Boutons

### Boutons modernes avec animations

```html
<!-- Bouton principal (bleu) -->
<button class="btn-modern btn-primary-pastel">
  <span>✨</span> Action
</button>

<!-- Bouton secondaire (rose) -->
<button class="btn-modern btn-secondary-pastel">
  Action
</button>

<!-- Bouton succès (vert menthe) -->
<button class="btn-modern btn-success-pastel">
  <span>✓</span> Valider
</button>

<!-- Bouton danger (corail) -->
<button class="btn-modern btn-danger-pastel">
  <span>🗑️</span> Supprimer
</button>

<!-- Bouton outline -->
<button class="btn-modern btn-outline-pastel">
  Détails
</button>
```

### Animations au survol
- Effet de vague au clic
- Élévation au survol
- Transition fluide (300ms)

---

## 📦 Cartes

### Carte moderne
```html
<div class="card-modern">
  <h3>Titre</h3>
  <p>Contenu de la carte</p>
</div>
```
**Effet** : Barre bleue animée en haut au survol + élévation

### Carte pour annonces
```html
<div class="card-announce">
  <h3>Mon annonce</h3>
  <p>Description...</p>
</div>
```
**Effet** : Barre bleue à gauche + glissement à droite au survol

---

## 🏷️ Badges

### Badges avec couleurs
```html
<!-- Différentes couleurs -->
<span class="badge-pastel badge-blue">Bleu</span>
<span class="badge-pastel badge-pink">Rose</span>
<span class="badge-pastel badge-mint">Vert</span>
<span class="badge-pastel badge-lavender">Lavande</span>
<span class="badge-pastel badge-peach">Pêche</span>
```

**Effet** : Scale up (1.05x) au survol

---

## ✨ Animations

### Classes d'animation

```html
<!-- Apparition douce -->
<div class="animate-fade-in">Contenu</div>

<!-- Slide depuis la droite -->
<div class="animate-slide-right">Contenu</div>

<!-- Slide depuis la gauche -->
<div class="animate-slide-left">Contenu</div>

<!-- Scale in -->
<div class="animate-scale-in">Contenu</div>

<!-- Bounce -->
<div class="animate-bounce">Contenu</div>

<!-- Pulse -->
<div class="animate-pulse">Contenu</div>
```

### Délais pour animations en cascade
```html
<div class="animate-fade-in delay-100">Premier</div>
<div class="animate-fade-in delay-200">Deuxième</div>
<div class="animate-fade-in delay-300">Troisième</div>
```

### Animation dynamique avec Angular
```html
<div *ngFor="let item of items; let i = index"
     class="animate-fade-in"
     [style.animation-delay]="(i * 100) + 'ms'">
  {{ item }}
</div>
```

---

## 📊 Tables

### Table moderne
```html
<table class="table-modern">
  <thead>
    <tr>
      <th>Colonne 1</th>
      <th>Colonne 2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Donnée 1</td>
      <td>Donnée 2</td>
    </tr>
  </tbody>
</table>
```

**Effet** : En-tête avec dégradé bleu + hover sur les lignes

---

## 🔗 Navigation

### Liens de navigation modernes
```html
<a routerLink="/page" class="nav-link-modern">
  <span>🏠</span> Accueil
</a>
```

**Effet** : Soulignement animé au survol

---

## 🖼️ Images

### Images avec effet moderne
```html
<img src="..." class="img-modern" alt="...">
```

**Effet** : Scale up (1.05x) + ombre au survol

---

## 🎯 Classes utilitaires

### Container moderne
```html
<div class="container-modern">
  <!-- Contenu centré avec max-width: 1200px -->
</div>
```

### Grille responsive
```html
<div class="grid-modern">
  <div>Item 1</div>
  <div>Item 2</div>
  <div>Item 3</div>
</div>
```
**Résultat** : Grille auto-responsive (min 300px par colonne)

### Texte avec dégradé
```html
<h1 class="text-gradient">Titre avec dégradé</h1>
```

### Séparateur
```html
<hr class="divider-pastel">
```

### Spinner de chargement
```html
<div class="spinner-pastel"></div>
```

---

## 📱 Responsive

Toutes les classes sont optimisées pour mobile :
- Container moderne : padding adaptatif
- Grille : colonnes flexibles
- Boutons : taille tactile optimale

---

## 💡 Conseils d'utilisation

1. **Cohérence des couleurs** : Utilisez la palette pastel pour toutes les couleurs
2. **Animations** : Utilisez les délais pour créer des effets en cascade
3. **Boutons** : Ajoutez des icônes emoji pour plus de clarté
4. **Cartes** : Privilégiez `card-modern` pour les contenus généraux
5. **Variables CSS** : Utilisez `var(--pastel-blue-400)` dans vos styles personnalisés

---

## 🚀 Exemple complet

```html
<div class="container-modern">
  <!-- En-tête -->
  <h1 class="text-gradient animate-fade-in">Mon titre</h1>
  <hr class="divider-pastel">
  
  <!-- Grille de cartes -->
  <div class="grid-modern">
    <div class="card-modern animate-scale-in delay-100">
      <h3>Carte 1</h3>
      <p>Contenu...</p>
      <button class="btn-modern btn-primary-pastel">
        <span>✨</span> Action
      </button>
    </div>
    
    <div class="card-modern animate-scale-in delay-200">
      <h3>Carte 2</h3>
      <span class="badge-pastel badge-blue">Nouveau</span>
      <button class="btn-modern btn-outline-pastel">Voir</button>
    </div>
  </div>
</div>
```

---

## 🎨 Personnalisation

Pour modifier les couleurs, éditez `src/theme-variables.css` :

```css
:root {
  --pastel-blue-400: #VotreCouleur;
  --gradient-blue: linear-gradient(...);
}
```

---

**Créé avec 💙 pour Evalia**

