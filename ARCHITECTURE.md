# Architecture de l’application de souscription

Ce document décrit l’architecture de l’application `subscription`, construite avec Spring Boot 4, une architecture hexagonale et des principes DDD.

---

## Vue d’ensemble

- **Type d’application** : API REST Spring Boot (Spring MVC)
- **Langage** : Java 17
- **Build** : Maven ([pom.xml](pom.xml))
- **Base de données** : PostgreSQL en prod, H2 en test
- **Persistence** : Spring Data JPA
- **Documentation API** : springdoc-openapi (UI Swagger via `springdoc-openapi-starter-webmvc-ui`)

Le code est structuré en trois grandes couches alignées avec l’architecture hexagonale :

- **Domain** : coeur métier, modèles et ports
- **Application** : cas d’usage (orchestration métier + ports)
- **Infrastructure** : adaptateurs primaires (HTTP) et secondaires (persistence)

---

## Organisation des packages

Racine : [src/main/java/com/koalanoir/souscription](src/main/java/com/koalanoir/souscription)

### 1. Démarrage Spring Boot

- [SouscriptionApplication.java](src/main/java/com/koalanoir/souscription/SouscriptionApplication.java)
  - Annoté avec `@SpringBootApplication` (et `@EnableAutoConfiguration`).
  - Point d’entrée `main` qui démarre le contexte Spring et expose les endpoints HTTP définis dans l’infrastructure primaire.

### 2. Couche Domain (Core métier)

Chemin : [src/main/java/com/koalanoir/souscription/domain](src/main/java/com/koalanoir/souscription/domain)

Responsabilités :
- Définir les **entités métier** et leurs invariants.
- Définir les **ports de sortie** (interfaces de repository) nécessaires aux cas d’usage.
- Ne pas dépendre de Spring, JPA ou de la couche HTTP.

Principaux éléments :

- Modèles : [domain/models](src/main/java/com/koalanoir/souscription/domain/models)
  - `Entity<TId>` : classe de base pour les entités du domaine.
  - `User` : agrégat représentant un utilisateur client.
  - `Subscription` : entité représentant une souscription.
- Exceptions : [domain/exceptions](src/main/java/com/koalanoir/souscription/domain/exceptions)
  - `DomainException` : base pour les erreurs métier.
- Ports de sortie : [domain/repository](src/main/java/com/koalanoir/souscription/domain/repository)
  - `UserRepository`
  - `SubscriptionRepository`

> Remarque : `Subscription` s’appuie désormais sur l’énumération métier `SubscriptionStatus` définie dans le domaine ; les entités JPA et les mappers de l’infrastructure dépendent de ce type, ce qui respecte le sens des dépendances de l’architecture hexagonale.

### 3. Couche Application (Use cases)

Chemin : [src/main/java/com/koalanoir/souscription/application/usecases](src/main/java/com/koalanoir/souscription/application/usecases)

Responsabilités :
- Implémenter les **cas d’usage** métier (orchestration) en s’appuyant sur les ports de sortie du domaine.
- Encapsuler la logique de workflow (validation simple, appels aux repositories, construction des entités).
- Ne pas contenir de logique technique HTTP ou JPA.

Use cases principaux :

- `CreateUserUseCase`
  - Reçoit un modèle d’entrée applicatif `CreateUserCommand` (name, phone, email).
  - Crée l’entité métier `User` et persiste via `UserRepository`.
- `CreateSubscriptionUseCase`
  - Dépend de `SubscriptionRepository`.
  - Méthode `create(String userId, String offerId, SubscriptionStatus status)` :
    - Vérifie l’absence de souscription existante via `repo.findById(userId)`.
    - Crée une nouvelle `Subscription` avec un `UUID` comme identifiant, puis la sauvegarde (`repo.save`).
  - Méthode `handle(CreateSubscriptionCommand command, User user)` :
    - Orchestration applicative : prend un `CreateSubscriptionCommand` et un `User` en entrée, appelle `create` avec un statut `ACTIVE`, et retourne la `Subscription` domaine.

> Remarque : les use cases utilisent des modèles d’entrée applicatifs (`CreateUserCommand`, `CreateSubscriptionCommand`) indépendants de la couche HTTP ; le DTO `CreateSubscriptionRequest` est mappé vers ces modèles dans le contrôleur, ce qui renforce l’indépendance de la couche application.

### 4. Couche Infrastructure

Chemin : [src/main/java/com/koalanoir/souscription/infrastructure](src/main/java/com/koalanoir/souscription/infrastructure)

La couche infrastructure est découpée en **adaptateurs primaires** (entrée) et **secondaires** (sortie).

#### 4.1. Infrastructure primaire (HTTP – WebMVC)

Chemin : [src/main/java/com/koalanoir/souscription/infrastructure/primary](src/main/java/com/koalanoir/souscription/infrastructure/primary)

- Config : [config](src/main/java/com/koalanoir/souscription/infrastructure/primary/config)
  - `EnvConfig` : chargement des variables d’environnement via `dotenv-java`.
  - `OpenApiConfig` : configuration de springdoc-openapi (génération du schéma OpenAPI, UI Swagger).
- DTOs : [dtos](src/main/java/com/koalanoir/souscription/infrastructure/primary/dtos)
  - `CreateSubscriptionRequest` : record/DTO HTTP pour les données d’entrée de la création de souscription.
  - `CreateSubscriptionResponse` : record/DTO HTTP pour la réponse, avec factory `fromDomain(Subscription, User)`.
- Contrôleurs : [controllers](src/main/java/com/koalanoir/souscription/infrastructure/primary/controllers)
  - `SubscriptionController` :
    - Annoté avec `@RestController` et `@RequestMapping("/api/v1/subscriptions")`.
    - Injecte `CreateUserUseCase` et `CreateSubscriptionUseCase`.
    - Endpoint `POST /api/v1/subscriptions` :
      - Reçoit un `CreateSubscriptionRequest`.
      - Mappe le DTO HTTP vers `CreateUserCommand` et `CreateSubscriptionCommand`.
      - Crée l’utilisateur via `createUserUseCase.handle(userCommand)` → `User` domaine.
      - Crée la souscription via `createSubscriptionUseCase.handle(subscriptionCommand, user)` → `Subscription` domaine.
      - Convertit le résultat en `CreateSubscriptionResponse.fromDomain(sub, user)` et renvoie un `ResponseEntity`.
    - Annotations OpenAPI (`@Operation`, `@ApiResponses`) pour la documentation.

La logique métier est volontairement déléguée aux use cases, le contrôleur ne gère que :
- la réception et la validation basique des entrées HTTP,
- l’appel aux cas d’usage,
- le mapping vers des DTOs de réponse.

#### 4.2. Infrastructure secondaire (Persistence – JPA)

Chemin : [src/main/java/com/koalanoir/souscription/infrastructure/secondary/persistence](src/main/java/com/koalanoir/souscription/infrastructure/secondary/persistence)

Responsabilités :
- Implémenter les **ports de sortie** du domaine à l’aide de JPA / Spring Data.
- Mapper les entités JPA vers les entités du domaine et réciproquement.
- Configurer les détails techniques de la base de données (mapping JPA, types, etc.).

Principaux éléments (structure observée dans `target/`) :
- Modèles JPA : [models](src/main/java/com/koalanoir/souscription/infrastructure/secondary/persistence/models)
  - Entités `*Entity` pour `User`, `Subscription`.
  - Enum `SubscriptionStatus` (utilisée notamment par `Subscription`).
- Mappers : [mappers](src/main/java/com/koalanoir/souscription/infrastructure/secondary/persistence/mappers)
  - Conversion entités JPA ↔ modèles de domaine.
- Repositories Spring Data : [repositories](src/main/java/com/koalanoir/souscription/infrastructure/secondary/persistence/repositories)
  - Interfaces `JpaRepository` implémentant `UserRepository` et `SubscriptionRepository`.

Les adaptateurs secondaires encapsulent toute la logique d’accès aux données pour que les use cases ne dépendent que des **ports** déclarés dans le domaine.

---

## Configuration et profils

- Config applicative : [src/main/resources/application.properties](src/main/resources/application.properties)
- Config de test : [src/test/resources/application-test.properties](src/test/resources/application-test.properties)
- Utilisation de `dotenv-java` pour charger les variables d’environnement (par exemple les paramètres de connexion PostgreSQL) et les exposer via `EnvConfig`.

---

## Tests

Chemin : [src/test/java/com/koalanoir/souscription](src/test/java/com/koalanoir/souscription)

- Tests de use cases :
  - [application/usecases](src/test/java/com/koalanoir/souscription/application/usecases)
    - `CreateUserUseCaseTest`
    - `CreateSubscriptionUseCaseTest`
  - Utilisent le pattern AAA (Arrange – Act – Assert) et des mocks de ports pour tester la logique métier en isolation.

- Tests REST (Web MVC) :
  - [infrastructure/primary/controllers](src/test/java/com/koalanoir/souscription/infrastructure/primary/controllers)
  - Utilisent `@WebMvcTest` et `MockMvc` pour tester le contrôleur `SubscriptionController` sans charger toute l’infrastructure.

- Tests de démarrage :
  - `SouscriptionApplicationTests` : vérifie le contexte Spring global.

---

## Principes d’architecture et bonnes pratiques

- **Hexagonal / DDD** : séparation stricte entre domaine, application et infrastructure.
- **Spring MVC uniquement** :
  - `spring-boot-starter-webmvc` utilisé, pas de WebFlux.
  - Aucun type réactif (Mono/Flux) dans le code.
- **Dépendances** :
  - Le domaine expose des ports (`UserRepository`, `SubscriptionRepository`).
  - Les use cases dépendent de ces ports.
  - Les adaptateurs secondaires implémentent les ports via Spring Data JPA.
  - Les adaptateurs primaires (controllers) appellent les use cases et exposent les DTOs HTTP.
- **DTOs vs Domain** :
  - Les DTOs (`*Request`, `*Response`) sont confinés à la couche infrastructure primaire.
  - Le domaine manipule des entités et value objects propres.

---

## Pistes d’amélioration

- **Isoler totalement la couche application** :
  - Centraliser le mapping HTTP → modèles applicatifs dans les contrôleurs.
  - Conserver des modèles d’entrée/sortie applicatifs (`*Command`, réponses de use case) indépendants de tout framework.
- **Renforcer les invariants métier** :
  - Encapsuler davantage de règles dans les entités (`Subscription`, `User`) ou value objects.
- **Couverture de tests** :
  - Ajouter des tests unitaires supplémentaires sur les règles métier (statut de souscription, déduplication, etc.).

Ce document peut être complété au fur et à mesure de l’évolution de l’architecture (nouveaux cas d’usage, nouveaux adaptateurs, nouveaux agrégats, etc.).