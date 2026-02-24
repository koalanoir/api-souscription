# 🧪 Testing Guide — Mockito Standard for Use Cases & Controllers

Ce document décrit comment écrire et maintenir des tests unitaires dans le projet en utilisant **JUnit 5 + Mockito**.

Il sert de référence commune pour assurer une qualité homogène, reproductible, et compatible avec l’architecture hexagonale.

---

# 🎯 1. Objectifs

- Tester les **Use Cases** en isolant les **ports** (repositories, services externes).  
- Garantir que la **logique métier** est testée sans dépendances techniques.  
- Standardiser l’utilisation de **Mockito** dans tout le projet.  
- Rendre les tests clairs, lisibles, robustes, et rapides.

---

# 🧱 2. Règles générales Mockito

### ✔ Toujours isoler la couche Application
Les **Use Cases** doivent être testés **sans Spring**, via :

```java
@ExtendWith(MockitoExtension.class)
class MyUseCaseTest { ... }
```
✔ Tous les ports sont mockés
Exemple pour CreateUserUseCase :
```java
@Mock
private UserRepository userRepository;

@InjectMocks
private CreateUserUseCase createUserUseCase;
```

✔ Aucune interaction réelle avec la base

save(), findById(), findByEmail(), etc. → toujours mockés.

✔ Pattern obligatoire : AAA

Arrange → préparer les mocks + données
Act → appeler la méthode testée
Assert → vérifier résultats + interactions Mockito

Use Cases — Tests obligatoires à écrire
Pour chaque Use Case, écrire au moins 3 tests :
1. Happy path (cas où tout fonctionne)

Le repository renvoie Optional.empty()
Le Use Case appelle save()
Le résultat est conforme

2. Cas d’erreur métier
Exemples :

Email déjà existant
Souscription déjà existante
Données invalides

3. Vérification des interactions

verify(repo).save(...)
verify(repo, never()).save(...) si erreur

### ✔ Pour les Controllers, utiliser @WebMvcTest


```java 
import org.mockito.Mock;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CreateSubscriptionUseCase createSubscriptionUseCase;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("POST /api/v1/subscriptions should return 400 when email is invalid")
    void subscribe_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        // Arrange
        Map<String, Object> payload = validPayload();
        payload.put("email", "email-invalide");

        // Act & Assert
        mockMvc.perform(post("/api/v1/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createUserUseCase, createSubscriptionUseCase);
    }
}
```