# TDD_BRIEF (source de vérité courte)

## Incrément en cours
- Objectif : Poser les tests RED de validation des données utilisateur (API + domaine).
- Context (facts utiles, 5-10 lignes max) : L’endpoint `POST /api/v1/subscriptions` doit rejeter email invalide, téléphone vide et nom vide. Le domaine `User` doit porter ces invariants pour éviter des souscriptions invalides.
- Acceptance criteria (tests attendus) : email invalide => HTTP 400 ; téléphone vide => HTTP 400 ; nom vide => HTTP 400 ; constructeur `User` lève `IllegalArgumentException` pour ces cas.
- Contraintes (non-négociables) : tests uniquement (RED), pas de code de production.

## Décisions / hypothèses (si indispensables)
- Décisions prises (ex: choix technique, compromis, etc.):
- Hypothèses (ex: sur le besoin, les tests, etc.) à valider ou invalider dans l’incrément courant :


# fichiers clés
- Tests : src/test/java/com/koalanoir/souscription/infrastructure/primary/controllers/SubscriptionControllerValidationWebMvcTest.java ; src/test/java/com/koalanoir/souscription/domain/models/UserValidationTest.java
- Prod : src/main/java/com/koalanoir/souscription/infrastructure/primary/controllers/SubscriptionController.java ; src/main/java/com/koalanoir/souscription/infrastructure/primary/dtos/CreateSubscriptionRequest.java ; src/main/java/com/koalanoir/souscription/domain/models/User.java

## Prochaine étape
- (RED | GREEN | REFACTOR) : REFACTOR


