# Chapitre 8 — Partie II : questions 11 à 17

Cette partie fait évoluer le code de la partie I. La signature du professeur reste :

```java
String simuler(Agence agence, int nbJours, VehiculeOffre offre);
```

## Question 11 — Résoudre le problème du service basique

Avant : `IAgenceService` imposait `simuler` à `BasicAgenceServiceImpl`, qui lançait une exception.

Après : `ISimulationAgenceService` contient uniquement `simuler`. `AgenceServiceImpl` implémente cette interface et `IAgenceService`. Le service basique implémente seulement `IAgenceService` et ne contient plus de fausse simulation.

C'est le principe ISP (séparation des interfaces) : une classe n'est pas obligée de proposer une opération qu'elle ne sait pas effectuer.

Fichiers : `service/ISimulationAgenceService.java`, `service/IAgenceService.java`, `service/BasicAgenceServiceImpl.java`, `service/AgenceServiceImpl.java`, `web/controller/AgenceController.java`.

Le contrôleur reçoit séparément `IAgenceService` pour chercher l'agence et `ISimulationAgenceService` pour simuler.

## Question 12 — Compiler

Commande exécutée :

```powershell
.\mvnw.cmd -DskipTests compile
```

La compilation est réussie. Le résultat final est aussi vérifié par les tests complets.

## Question 13 — Créer le package io

Le package `tn.esprit.autoloc.io` regroupe la présentation des rapports. Le service des agences calcule le total ; les classes de ce package construisent ou affichent le rapport.

## Question 14 — Factory et Decorator

`IRapportAgence` définit `ecrire(Agence agence, double total)`.

- `RapportTotal` retourne `Total : ...` et un saut de ligne.
- `RapportTexteDecorator` enveloppe `RapportTotal` et ajoute l'en-tête dans le texte retourné.
- `RapportConsoleDecorator` enveloppe le même rapport, affiche l'en-tête avec `System.out.println`, puis retourne le total fourni par `RapportTotal`.
- `RapportFactory` fournit le rapport approprié selon le mode configuré : `TEXTE` ou `CONSOLE`.
- `ModeRapport` contient ces deux valeurs.

Les décorateurs portent `@Component` : Spring crée les objets et injecte leurs dépendances. Leur comportement s'ajoute par délégation, sans modifier `RapportTotal`.

Dans `AgenceServiceImpl`, la boucle du professeur additionne toujours les tarifs. Seule la construction du rapport est déléguée :

```java
return rapportFactory.creer().ecrire(agence, total);
```

La signature de `simuler` ne change pas. Le résultat reste un `String`, y compris en mode console.

### Choisir le mode

Par défaut, `application.properties` contient :

```properties
autoloc.rapport.mode=${RAPPORT_MODE:TEXTE}
```

Mode TEXTE (par défaut), réponse Postman :

```text
Id : 1; Nom : Agence ariana; Tel : 71585874
Total : 7200.0
```

Pour le mode CONSOLE, dans **Run → Edit Configurations → AutolocApiApplication-local**, ajouter la variable d'environnement **RAPPORT_MODE=CONSOLE**, en conservant les autres variables existantes. Redémarrer l'application.

Dans ce mode, la console IntelliJ affiche :

```text
Id : 1; Nom : Agence ariana; Tel : 71585874
```

Postman reçoit seulement :

```text
Total : 7200.0
```

Pour revenir à deux lignes dans Postman, remettre **RAPPORT_MODE=TEXTE** puis redémarrer. Il n'est pas nécessaire de changer l'URL ou la signature de `simuler`.

## Question 15 — Refaire les tests

Tests automatiques :

```powershell
.\mvnw.cmd test
```

Résultat : **34 tests réussis, 0 échec, 0 erreur**.

- `SimulationTests` contrôle les trois offres, les montants proches du seuil de 2000, les agences sans véhicule, les erreurs HTTP et le rapport TEXTE exact.
- `ConsoleSimulationTests` démarre Spring avec le mode CONSOLE, teste les trois offres, vérifie le texte retourné et capture l'en-tête effectivement affiché dans la console.
- Les tests CRUD et Swagger existants continuent de passer.

Pour Postman :

1. Démarrer MySQL puis **AutolocApiApplication-local** ; garder l'application lancée.
2. Importer `postman/Chap8-Partie2.postman_collection.json` et utiliser l'environnement `local` existant (`asiApiUrl=http://localhost:8080/api`).
3. Dans les variables de la collection, régler `rapportMode` sur le même mode que le serveur : `TEXTE` ou `CONSOLE`.
4. Exécuter toute la collection dans l'ordre, une itération. Les premières requêtes créent une agence et un véhicule de test ; les dernières les suppriment.

La variable Postman `rapportMode` sert aux assertions : elle ne change pas la configuration du serveur.

Les trois requêtes sont des GET et utilisent toutes **40 jours** :

```text
{{asiApiUrl}}/agences/{{simulationAgenceId}}/simulation?jours=40&offre=STANDARD
{{asiApiUrl}}/agences/{{simulationAgenceId}}/simulation?jours=40&offre=WEEKEND
{{asiApiUrl}}/agences/{{simulationAgenceId}}/simulation?jours=40&offre=LONGUE_DUREE
```

Pour le véhicule de test à 100/jour, les totaux attendus sont 4000.0, 3200.0 et 2850.0. L'en-tête est présent dans la réponse seulement en mode TEXTE.

Vérification sur MySQL effectuée dans les deux modes : les scripts de la collection ont été exécutés via un lanceur Node local reproduisant les appels Postman utilisés par ces scripts, avec **13 assertions réussies en TEXTE et 13 en CONSOLE**. L'en-tête du mode CONSOLE a également été constaté dans la sortie de l'application. Le Runner graphique Postman n'a pas été utilisé. L'application a ensuite été remise en mode TEXTE sur le port 8080.

## Question 16 — Strategy avec Map et Spring IoC

Le calcul est séparé en quatre fichiers dans `calcul` :

| Fichier | Responsabilité |
| --- | --- |
| `ITarifStrategy.java` | Contrat commun : offre associée et calcul |
| `StandardTarifStrategy.java` | Prix journalier × jours |
| `WeekendTarifStrategy.java` | Prix journalier × 0,8 × jours |
| `LongueDureeTarifStrategy.java` | Prix journalier × 0,75 × jours, puis × 0,95 si le total dépasse 2000 |

Chaque classe concrète porte `@Component`. Spring les injecte dans le constructeur de `VehiculeServiceImpl` sous forme de `List<ITarifStrategy>`.

Le constructeur remplit une `Map<VehiculeOffre, ITarifStrategy>`, réalisée par une `EnumMap`. `calculTarif` sélectionne la stratégie dans la map puis lui délègue le calcul. Les conditions de la partie I sont donc remplacées par le choix d'un objet spécialisé.

Les formules et l'ordre des opérations du professeur sont conservés dans les stratégies. La signature publique de `calculTarif` ne change pas.

## Question 17 — Pousser le changement

Le dépôt distant configuré est `https://github.com/ons-m/Spring-Boot-Ons-.git`. L'envoi concerne les fichiers de l'atelier chapitre 8, avec les interfaces de la partie I nécessaires à sa compilation. Les suppressions d'images et autres modifications sans rapport avec cette partie restent séparées.

## Inventaire de la partie II

Les chemins Java ci-dessous sont relatifs à `src/main/java/tn/esprit/autoloc/`.

Fichiers créés :

- `service/ISimulationAgenceService.java`.
- `io/IRapportAgence.java`, `io/ModeRapport.java`, `io/RapportTotal.java`, `io/RapportTexteDecorator.java`, `io/RapportConsoleDecorator.java`, `io/RapportFactory.java`.
- `calcul/ITarifStrategy.java`, `calcul/StandardTarifStrategy.java`, `calcul/WeekendTarifStrategy.java`, `calcul/LongueDureeTarifStrategy.java`.
- `src/test/java/tn/esprit/autoloc/ConsoleSimulationTests.java`.
- `postman/Chap8-Partie2.postman_collection.json`.
- `CHAP8-PARTIE2.md`.

Fichiers modifiés :

- `service/IAgenceService.java`, `service/BasicAgenceServiceImpl.java`, `service/AgenceServiceImpl.java`.
- `service/VehiculeServiceImpl.java`.
- `web/controller/AgenceController.java`.
- `src/main/resources/application.properties`.
- `src/test/java/tn/esprit/autoloc/SimulationTests.java`.
