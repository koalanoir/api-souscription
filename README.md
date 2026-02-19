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
- **Framework** : Spring Boot (WebMVC, Data JPA)
- **BDD** : PostgreSQL (profil par défaut), H2 en mémoire pour les tests
- **Documentation d'API** : springdoc-openapi-starter-webmvc-ui
- **Build & tests** : Maven, JUnit 5, Mockito

## 🧱 Architecture & DDD

L’architecture hexagonale et l’organisation détaillée des couches (Domain, Application, Infrastructure), ainsi que les schémas de flux et de dépendances, sont décrits dans le document dédié :

- [ARCHITECTURE.md](ARCHITECTURE.md)

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
