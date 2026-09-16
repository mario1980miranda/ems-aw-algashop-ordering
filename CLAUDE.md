# Ordering service

Microservice Spring Boot 3.4 / Java 21 / Gradle, en DDD tactique. Pour l'instant : couche domaine uniquement.

## Commandes
- Tests : `./gradlew test`
- Build : `./gradlew build`

## Organisation du package `com.algaworks.algashop.ordering.domain`
- `entity/` : aggregates et entités (`Order`, `OrderItem`, `Customer`), enums de statut
- `valueobject/` : value objects immuables ; `valueobject/id/` pour les identifiants typés
- `exception/` : exceptions métier héritant de `DomainException`, messages dans `ErrorMessages`
- `factory/` : construction d'aggregates complexes
- `utility/` : `IdGenerator`, `FieldValidations`

## Conventions
- Value objects : validation dans le constructeur (`Objects.requireNonNull`, `FieldValidations`), aucun setter, opérations qui retournent une nouvelle instance
- Entités : setters privés, invariants vérifiés dans les setters ; reconstruction depuis la persistance via `@Builder(builderClassName = "ExistingXBuilder", builderMethodName = "existing")`, création via une factory statique (`Order.draft(...)`)
- Pas d'annotations Spring/JPA dans `domain`
- Lombok : `@Builder` uniquement ; pas de `@Data` ni `@Setter` sur les entités

## Tests
- JUnit 5 + AssertJ, une classe de test par classe de domaine
- Nommage : `shouldDoSomething`, `shouldNotAllowX`
- Données de test via `XTestDataBuilder` (voir `OrderTestDataBuilder`), jamais de `new` inline complexe dans les tests
- Toujours lancer `./gradlew test` après une modification du domaine