# AutoLoc API

Projet Spring Boot de location de véhicules : Spring MVC, JPA/MySQL, documentation OpenAPI et design patterns GoF.

## Démarrage

Prérequis : JDK 17 ou supérieur et MySQL sur le port 3306. Ouvrir `pom.xml` comme projet Maven dans IntelliJ, puis recharger Maven.

La configuration utilise la base `autoloc_db`, créée automatiquement si les droits MySQL le permettent, et l'utilisateur `root`. Le mot de passe est lu depuis `DB_PASSWORD` et reste vide si cette variable n'est pas définie.

Dans PowerShell, si MySQL exige un mot de passe :

```powershell
$env:DB_PASSWORD = 'votre_mot_de_passe_mysql'
.\mvnw.cmd spring-boot:run
```

Sans mot de passe, lancer directement `.\mvnw.cmd spring-boot:run`. Attendre `Started AutolocApiApplication` et garder le processus lancé pendant les tests Postman.

- API : `http://localhost:8080/api`
- Swagger : `http://localhost:8080/api/swagger-ui/index.html`
- OpenAPI : `http://localhost:8080/api/v3/api-docs`

## Ressources REST

Les agences, véhicules, clients, employés, réservations, contrats, maintenances et équipements disposent des opérations CRUD. Les paiements sont consultables uniquement ; ils peuvent être créés avec un contrat.

Les réponses utilisent notamment 201 pour la création, 204 pour la suppression sans corps, 400 pour une requête invalide, 404 pour une ressource absente et 405 pour une méthode HTTP non autorisée. Les erreurs partagent le format `ErrorResponse`.

## Simulation et chapitre 8

```text
GET /api/agences/1/simulation?jours=40&offre=STANDARD
GET /api/agences/1/simulation?jours=40&offre=WEEKEND
GET /api/agences/1/simulation?jours=40&offre=LONGUE_DUREE
```

Remplacer `1` par un identifiant existant. Les offres sont calculées par des stratégies Spring choisies dans une `Map`. WEEKEND applique 20 % de réduction ; LONGUE_DUREE applique 25 %, puis 5 % supplémentaires si le total réduit du véhicule dépasse 2000.

Le mode `TEXTE`, utilisé par défaut, retourne l'en-tête et le total. Avec la variable d'environnement `RAPPORT_MODE=CONSOLE`, l'en-tête est affiché dans la console et seul le total est retourné. Redémarrer l'application après un changement de mode.

Le service de simulation est séparé du contrat CRUD. `IAgenceCompleteService` regroupe les deux interfaces et reste disponible sans être implémentée par les classes actuelles.

Voir [l'explication des questions 11 à 17](CHAP8-PARTIE2.md) pour Factory, Decorator, Strategy et la procédure de vérification.

## Tests

```powershell
.\mvnw.cmd test
```

Les tests utilisent H2 en mémoire et ne nécessitent pas MySQL. La suite couvre le CRUD, les relations, les erreurs HTTP, Swagger, les offres et les deux modes de rapport.

Dans Postman, importer l'environnement `postman/local.postman_environment.json`, sélectionner **local**, puis importer la collection souhaitée :

| Collection | Vérifications |
| --- | --- |
| `postman/AutoLoc-API.postman_collection.json` | CRUD, paiements et documentation du chapitre 6 |
| `postman/Chap8-Partie1.postman_collection.json` | Trois offres pour 40 jours, mode TEXTE |
| `postman/Chap8-Partie2.postman_collection.json` | Trois offres pour 40 jours, modes TEXTE et CONSOLE |

Exécuter les collections dans l'ordre fourni, avec une itération. Elles créent et nettoient leurs données de test. Pour la partie II, la variable de collection `rapportMode` doit correspondre au mode du serveur ; elle ne modifie pas le serveur.

## Organisation

- `domain` : entités, relations JPA et validation.
- `repository` : accès aux données.
- `service` : contrats et logique métier.
- `calcul` : offres et stratégies de tarification.
- `io` : rapports, Factory et Decorator.
- `web` : contrôleurs, OpenAPI et traitement des erreurs.
- `src/test` : tests et configuration H2.

Les paramètres personnels d'IntelliJ et les fichiers générés dans `target` sont exclus de Git. Ne pas enregistrer de mot de passe dans le dépôt.
