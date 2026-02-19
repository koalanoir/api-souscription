# Copilot Instructions — Subscription Service (WebMVC + Hexagonal + DDD)

## 🎯 Objectif
Ces instructions guident GitHub Copilot afin qu'il génère du code conforme à l’architecture du projet :
- **Spring Boot 4 (WebMVC)**  
- **Architecture hexagonale** (Domain → Application → Infrastructure)  
- **DDD** (entités métier propres, invariants, ports)  
- **TDD‑first** (tests AAA, tests des use cases)

---

## 🧱 Architecture à respecter

### 1. Domain (core)
- Contient **uniquement** :  
  - entités métier (`User`, `Subscription`)  
  - value objects  
  - règles métier  
  - ports de sortie (`UserRepository`, `SubscriptionRepository`)
- **Ne jamais utiliser :**  
  - Spring  
  - JPA  
  - Annotations Hibernate  
  - DTO HTTP  
- Toujours maintenir des **invariants métier** dans le domaine.

### 2. Application (use cases)
- Contient les classes comme `CreateUserUseCase`, `CreateSubscriptionUseCase`.
- Orchestration simple : validation → appel aux ports → retour du domaine.
- Tous les use cases doivent avoir des **tests unitaires** isolés (mock ports).
- Pas de dépendance aux adapters techniques.

### 3. Infrastructure / Primary (HTTP – WebMVC)
- Utilise **`@RestController`** (pas WebFlux, pas Mono/Flux).  
  → Vérifier que Copilot ne propose jamais de types réactifs.
- Mappe DTO → UseCase → Domain → Réponse DTO.
- Controllers très simples (pas de logique métier).

### 4. Infrastructure / Secondary (Persistence – JPA)
- Contient :  
  - entités JPA (`*Entity`)  
  - repositories Spring Data (`JpaRepository`)  
  - mappers entité ↔ domaine
- Les repositories implémentent les **ports** du domaine.

---

## 🚫 Interdictions Copilot
- ❌ Proposer `spring-boot-starter-webflux` ou du code WebFlux (Mono/Flux).  
- ❌ Proposer des opérations bloquantes dans le domaine.  
- ❌ Mettre des annotations JPA dans les classes du domaine.  
- ❌ Faire des appels HTTP/DB dans un use case (seuls les ports le peuvent).  
- ❌ Générer des DTO dans le domaine.

*(Rappel : mélanger WebFlux + JPA conduit à des conflits d’auto-config et à du blocage non‑réactif, ce qui est explicitement déconseillé) 
---

## 🧪 Bonnes pratiques TDD à suivre (Copilot doit les appliquer)

### Tests unitaires (Use cases)
- AAA : **Arrange → Act → Assert**
- Mock des ports (`UserRepository`, `SubscriptionRepository`)
- Tester :  
  - invariants métier  
  - déduplication user  
  - absence de souscription déjà existante  
  - statut de souscription

### Tests REST (Integration – MVC)
- Utiliser **`@WebMvcTest`** pour tester le contrôleur seul.
- Utiliser **`MockMvc`** pour simuler les requêtes HTTP.


### Stratégie générale
- Toujours écrire **le test avant**  
- Favoriser des tests courts, lisibles et déterministes

---

## 📐 Conventions de code à appliquer
- Noms explicites (ex : `createSubscription`, `toDomain`, `toEntity`)
- Méthodes courtes (≤ 20 lignes)
- Pas de logique métier dans les controllers
- Utiliser des DTOs pour les requêtes/réponses HTTP, jamais dans le domaine
- Utiliser des records Java pour les DTOs immuables
- Distinguer strictement :
  - `*Request` / `*Response` (HTTP)
  - `*Entity` (JPA)
  - modèles du domaine

---

## 📘 Documentation & OpenAPI
- Toujours générer/mettre à jour le schéma OpenAPI.  
- Utiliser `springdoc-openapi` pour rester conforme.  
  *(OpenAPI est le standard recommandé pour documenter les API et générer du code client) [8](https://swagger.io/specification/)*

---

## 🔧 Instructions pour génération automatique de code
Quand Copilot génère du code :
1. Préférer **Spring MVC + JPA**  
2. Respecter la structure hexagonale existante  
3. Proposer systématiquement :
   - un **use case**
   - un **port**
   - un **adapter JPA**
   - les **tests unitaires**
   - les **DTOs** associés
4. Utiliser les conventions de nommage DDD (langage ubiquitaire)
5. Pour tout ajout métier → penser invariants + tests

---

## ✔️ Résultats attendus
Copilot doit :
- Générer du code **conforme** à l’architecture  
- Proposer des tests cohérents avec TDD  
- Ne jamais introduire WebFlux / réactivité  
- Préserver la séparation des couches  