# AlgaShop Ordering

Microservice de prise de commande d'AlgaShop (repo `ems-aw-algashop-ordering`, submodule `microservices/ordering` du repo meta).

**Stack** : Java 21 · Spring Boot 3.4 · Gradle · Lombok · JUnit 5 + AssertJ

> État actuel : **couche domaine uniquement** (DDD tactique). Pas encore d'API REST ni de persistance.

## Build et tests

```bash
./gradlew test     # tests unitaires du domaine
./gradlew build    # compilation + tests
```

## Organisation du code

Package racine : `com.algaworks.algashop.ordering.domain.model`

| Package | Contenu |
|---------|---------|
| `entity/` | Aggregates et entités : `Order`, `OrderItem`, `ShoppingCart`, `ShoppingCartItem`, `Customer` ; enums `OrderStatus`, `PaymentMethod` |
| `valueobject/` | Value objects immuables : `Money`, `Quantity`, `Product`, `ProductName`, `Address`, `Billing`, `Shipping`, `Recipient`, `Email`, `Phone`, `Document`, `FullName`, `BirthDate`, `ZipCode`, `LoyaltyPoints` |
| `valueobject/id/` | Identifiants typés : `OrderId`, `OrderItemId`, `ShoppingCartId`, `ShoppingCartItemId`, `CustomerId`, `ProductId` |
| `exception/` | Exceptions métier héritant de `DomainException`, messages centralisés dans `ErrorMessages` |
| `factory/` | `OrderFactory` : construction d'une commande complète |
| `utility/` | `IdGenerator`, `validator/FieldValidations` |

## Modèle métier

### Order (aggregate)

Création via `Order.draft(customerId)`.

**Cycle de vie** (`OrderStatus`) :

```
DRAFT ──place()──▶ PLACED ──markAsPaid()──▶ PAID ──markAsReady()──▶ READY
  │                  │                        │                        │
  └──────────────────┴────────── cancel() ────┴────────────────────────┘──▶ CANCELED
```

Toute transition non prévue lève `OrderStatusCannotBeChangedException`. Chaque transition renseigne sa date (`placedAt`, `paidAt`, `readyAt`, `canceledAt`).

**Règles** :
- Seule une commande `DRAFT` est modifiable (`addItem`, `removeItem`, `changeItemQuantity`, `changePaymentMethod`, `changeBilling`, `changeShipping`), sinon `OrderCannotBeEditedException`.
- `place()` exige livraison, facturation, moyen de paiement et au moins un item (`OrderCannotBePlacedException`).
- `addItem` refuse un produit hors stock (`ProductOutOfStockException`).
- `removeItem` sur un item absent lève `OrderDoesNotContainOrderItemException`.
- `totalAmount` et `totalItems` sont recalculés à chaque modification des items.

### ShoppingCart (aggregate)

Création via `ShoppingCart.startShopping(customerId)`.

| Opération | Comportement |
|-----------|--------------|
| `addItem(product, quantity)` | Refuse un produit hors stock ; si le produit est déjà présent, additionne la quantité et rafraîchit l'item |
| `removeItem(itemId)` | Retire l'item (`ShoppingCartDoesNotContainItemException` si absent) |
| `changeItemQuantity(itemId, quantity)` | Modifie la quantité d'un item |
| `refreshItem(product)` | Met à jour prix, nom et disponibilité depuis le produit (`ShoppingCartDoesNotContainProductException` si absent) |
| `empty()` | Vide le panier |
| `containsUnavailableItems()` / `isEmpty()` | Requêtes d'état |

Un `ShoppingCartItem` ne peut être rafraîchi qu'avec son propre produit (`ShoppingCartItemIncompatibleProductException`). Les totaux du panier et de chaque item sont recalculés automatiquement.

### Customer (aggregate)

Gestion du client : changement de nom, email, téléphone, adresse ; notifications promotionnelles ; points de fidélité. Un client archivé (`archive()`) n'est plus modifiable (`CustomerArchivedException`).

## Conventions

- **Value objects** : validation dans le constructeur, aucun setter, les opérations renvoient une nouvelle instance.
- **Entités** : setters privés qui vérifient les invariants ; création via une factory statique (`draft`, `startShopping`, `brandNew`) ; reconstruction depuis la persistance via le builder `existing()`.
- **Aucune annotation Spring/JPA** dans le domaine ; Lombok limité à `@Builder`.
- **Tests** : une classe de test par classe de domaine (ou par comportement, ex. `OrderCancelTest`), méthodes `shouldDoSomething` / `shouldNotAllowX`, données construites avec les `XTestDataBuilder`.

Voir aussi [`CLAUDE.md`](CLAUDE.md) pour les instructions destinées à Claude Code (skill `/new-value-object`).
