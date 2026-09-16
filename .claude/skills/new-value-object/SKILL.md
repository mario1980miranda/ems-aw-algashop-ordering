---
name: new-value-object
description: Crée un value object du domaine ordering avec sa classe de test. Utiliser quand on demande d'ajouter un value object, un VO, ou un identifiant typé.
---

Créer le value object `$ARGUMENTS` en suivant exactement le style de `Money.java` et `MoneyTest.java` :

1. Lire `domain/valueobject/Money.java` et `domain/valueobject/Email.java` comme modèles
2. Créer la classe dans `domain/valueobject/` (ou `valueobject/id/` si c'est un identifiant, modèle `OrderId.java`)
3. Valider tous les arguments dans le constructeur ; aucun setter
4. Créer `XTest.java` dans `src/test/.../valueobject/` avec au minimum : cas nominal, null refusé, valeur invalide refusée
5. Lancer `./gradlew test` et corriger jusqu'au vert