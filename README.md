# Subscription Service (Souscription API)

## 🧾 Description du microservice

Ce microservice expose une API REST de **gestion des souscriptions** (subscriptions) pour des offres (offers) associées à des utilisateurs (users).

Il permet notamment :
- la **création d'un utilisateur** à partir des données client fournies,
- la **création d'une souscription** liée à cet utilisateur et à une offre métier,
- la persistance de ces informations dans une base PostgreSQL.

Le service est développé en **Java 17** avec **Spring Boot 4**, et documenté avec **springdoc-openapi** (Swagger UI).

## 🚀 Stack technique

- **Langage** : Java 17
- **Framework** : Spring Boot (WebMVC, WebFlux, Data JPA)
- **BDD** : PostgreSQL (profil par défaut), H2 en mémoire pour les tests
- **Documentation d'API** : springdoc-openapi-starter-webmvc-ui
- **Build & tests** : Maven, JUnit 5, Mockito

## 🧱 Architecture & DDD

Le projet applique une **architecture hexagonale** (Ports & Adapters) fortement inspirée du **Domain-Driven Design (DDD)**.

Organisation principale (package `com.koalanoir.souscription`) :

```text
com.koalanoir.souscription
├── application/                    # Couche application (use cases)
│   └── usecases/
│       ├── CreateSubscriptionUseCase
│       └── CreateUserUseCase
├── domain/                         # Cœur du domaine métier (DDD)
│   ├── models/                     # Entités de domaine (User, Subscription, Entity)
│   ├── repository/                 # Ports de sortie (interfaces de repository)
│   └── exceptions/                 # Exceptions métier
├── infrastructure/                 # Adapters techniques (entrée/sortie)
│   ├── primary/                    # Adapters d'entrée (HTTP)
│   │   ├── controllers/            # REST controllers (SubscriptionController)
│   │   ├── dtos/                   # DTOs exposés (CreateSubscriptionRequest, CreateSubscriptionResponse)
│   │   └── config/                 # Config (OpenAPI, env)
│   └── secondary/                  # Adapters de sortie (persistance)
│       └── persistence/
│           ├── models/             # Entités JPA (SubscriptionEntity, UserEntity, ...)
│           ├── repositories/       # Impl. des ports (SubscriptionRepositoryPostgres, UserRepositoryPostgres)
│           └── mappers/            # Mapping Domaine <-> JPA (SubscriptionMapper, UserMapper)
└── SouscriptionApplication         # Bootstrap Spring Boot
```

### Rôle des couches (DDD)

- **Domaine (`domain`)**
  - Contient les **entités métier** :
    - `User` : représente un client avec `id`, `name`, `phone`, `email`.
    - `Subscription` : représente une souscription, reliée à un `userId`, une `offerId` et un `SubscriptionStatus`.
    - `Entity<ID>` : classe de base avec `id`, `createdAt`, `updatedAt`, et logique d'égalité par identifiant.
  - Contient les **ports de sortie** sous forme d'interfaces (`UserRepository`, `SubscriptionRepository`) qui définissent ce dont le domaine a besoin pour persister/charger les objets.
  - Ne dépend pas de Spring, JPA ou toute autre technologie.

- **Application (`application/usecases`)**
  - Implémente les **use cases métiers** :
    - `CreateUserUseCase` :
      - Vérifie qu'aucun utilisateur n'existe déjà pour un email donné (`UserRepository.findByEmail`).
      - Crée un `User` avec un `UUID` et délègue la persistance au `UserRepository`.
      - Offre aussi une méthode `handle(CreateSubscriptionRequest)` qui extrait les données client du DTO HTTP.
    - `CreateSubscriptionUseCase` :
      - Vérifie qu'aucune souscription n'existe déjà pour un identifiant donné (`SubscriptionRepository.findById`).
      - Crée une `Subscription` avec un `UUID` et un statut (`SubscriptionStatus.ACTIVE`).
      - La méthode `handle(CreateSubscriptionRequest, User)` orchestre la création à partir du DTO et de l'utilisateur existant/nouveau.
  - Cette couche orchestre le flux entre **domaine** et **adapters** sans dépendre des détails techniques (HTTP, JPA).

- **Infrastructure primaire (`infrastructure/primary`)**
  - **Controllers** :
    - `SubscriptionController` expose l'endpoint HTTP principal :
      - `POST /api/v1/subscriptions`
      - Corps : `CreateSubscriptionRequest` (offerId, clientName, email, phoneNumber, subscriptionType)
      - Orchestration :
        1. `createUserUseCase.handle(request)` → crée ou renvoie un `User`.
        2. `createSubscriptionUseCase.handle(request, user)` → crée la `Subscription` associée.
        3. Retourne un `CreateSubscriptionResponse` construit via `CreateSubscriptionResponse.fromDomain(sub, user)`.
  - **DTOs** :
    - `CreateSubscriptionRequest` : contrat d'entrée HTTP, orienté API (noms fonctionnels, annotations Swagger).
    - `CreateSubscriptionResponse` : contrat de sortie HTTP, construit à partir des objets de domaine (`Subscription`, `User`).
  - **Config** :
    - `OpenApiConfig` : configuration OpenAPI / Swagger UI.
    - `EnvConfig` : intégration de `dotenv` pour la configuration via variables d'environnement.

- **Infrastructure secondaire (`infrastructure/secondary/persistence`)**
  - **Entités JPA** :
    - `SubscriptionEntity` mappée sur la table `subscriptions` avec :
      - `id`, `userId`, `offerId`, `subscribedAt` (LocalDate), `status` (enum `SubscriptionStatus`).
    - `UserEntity` (non montrée ici, mais symétrique) pour la table des utilisateurs.
  - **Mappers** :
    - `SubscriptionMapper` : fait le pont entre `Subscription` (domaine) et `SubscriptionEntity` (JPA), en gérant aussi la date de souscription (`subscribedAt`).
    - `UserMapper` : convertit `User` ↔ `UserEntity`.
  - **Repositories** :
    - `SubscriptionRepositoryPostgres` et `UserRepositoryPostgres` implémentent respectivement les ports de sortie `SubscriptionRepository` et `UserRepository`.
    - Ils délèguent à des `Jpa*Repository` Spring Data et effectuent le mapping vers/depuis le domaine via les mappers.

### Accents DDD

- **Dépendances orientées vers le domaine** :
  - Le domaine (`domain`) ne dépend que de lui-même.
  - Les use cases (`application`) dépendent du domaine via interfaces et modèles.
  - L'infrastructure dépend du domaine et des ports pour implémenter les détails techniques.

- **Séparation des modèles** :
  - Modèles de domaine (`User`, `Subscription`) ≠ Entités de persistance (`UserEntity`, `SubscriptionEntity`) ≠ DTO HTTP (`CreateSubscriptionRequest`, `CreateSubscriptionResponse`).
  - Cette séparation évite de « polluer » le domaine avec des contraintes techniques (JPA, JSON, HTTP).

- **Use cases explicites** :
  - Chaque opération métier importante est encapsulée dans une classe dédiée (`CreateUserUseCase`, `CreateSubscriptionUseCase`), ce qui
    - documente le langage ubiquitaire,
    - facilite les tests unitaires,
    - isole les règles métier (ex. unicité de l'email, non-duplication de souscription).

## 📐 Schémas d'architecture

### 1. Flux HTTP → Domaine → BDD

```text
Client HTTP
   ↓  (JSON CreateSubscriptionRequest)
SubscriptionController (REST)
   ↓  (DTO → UseCases)
CreateUserUseCase        CreateSubscriptionUseCase
   ↓                             ↓
   User (domain)          Subscription (domain)
        \                      /
         \                    /
          UserRepository    SubscriptionRepository (ports de sortie)
                    ↓              ↓
        UserRepositoryPostgres   SubscriptionRepositoryPostgres (adapters)
                    ↓              ↓
               JPA Repositories (Spring Data)
                    ↓
                 PostgreSQL
```

### 2. Couches logicielles (vue hexagonale simplifiée)

```text
          +-----------------------------+
          |       Infrastructure        |
          |   (primary & secondary)     |
          +-----------------------------+
             ↑                       ↑
             |                       |
     HTTP / DTOs                JPA / DB

          +-----------------------------+
          |         Application         |
          |       (Use Cases)           |
          +-----------------------------+
                       ↑
                       |
          +-----------------------------+
          |           Domain            |
          | (Entities, Repositories)    |
          +-----------------------------+
```

## 🔌 Configuration & exécution

### Configuration

Fichier principal : [src/main/resources/application.properties](src/main/resources/application.properties)

- Nom d'application : `spring.application.name=subscription-service`
- OpenAPI / Swagger :
  - `springdoc.api-docs.path=/api-docs`
  - `springdoc.swagger-ui.path=/swagger-ui.html`
- Datasource PostgreSQL (via variables d'environnement) :
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`

Pour les tests, un profil `test` utilise une base H2 en mémoire (voir [src/test/resources/application-test.properties](src/test/resources/application-test.properties)).

### Build & lancement

- Build :
  - `mvn clean package`
- Lancer les tests :
  - `mvn test`
- Lancer l'application localement :
  - `mvn spring-boot:run`

## ✅ Tests

Les tests unitaires sont écrits avec **JUnit 5** et **Mockito**, en respectant les bonnes pratiques AAA :

- Use cases applicatifs :
  - [CreateSubscriptionUseCaseTest](src/test/java/com/koalanoir/souscription/application/usecases/CreateSubscriptionUseCaseTest.java)
  - [CreateUserUseCaseTest](src/test/java/com/koalanoir/souscription/application/usecases/CreateUserUseCaseTest.java)
- Contrôleur REST :
  - [SubscriptionControllerTest](src/test/java/com/koalanoir/souscription/infrastructure/primary/controllers/SubscriptionControllerTest.java)

Chaque test isole son sujet (use case ou controller) en mockant les dépendances (repositories, autres use cases) afin de rester fidèle aux principes DDD et hexagonaux.
