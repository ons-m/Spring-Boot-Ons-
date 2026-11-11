# Réorganisation des véhicules — questions 1 à 9

## Préparation

Démarrer MySQL, puis lancer `.\mvnw.cmd spring-boot:run` depuis PowerShell.
Ouvrir Swagger : http://localhost:8090/api/swagger-ui/index.html
On peut aussi utiliser Postman, avec la base `http://localhost:8090/api`.

Créer les données avec les requêtes suivantes (Body / raw / JSON dans Postman,
en-tête `Content-Type: application/json`). Conserver les identifiants retournés :
`V` pour `idVehicule`, `A` pour `idAgence`, `E` pour `idEquipement`.

`POST /api/agences` :
```json
{"nom":"Agence Tunis","ville":"Tunis","adresse":"1 rue de Tunis","telephone":"71000000"}
```

`POST /api/vehicules` (utiliser une immatriculation différente si elle existe déjà) :
```json
{"immatriculation":"TEST-REORG-1","marque":"Toyota","modele":"Yaris","categorie":"UTILITAIRE","tarifJournalier":80,"statut":"DISPONIBLE"}
```

`POST /api/equipements` (libellé unique) :
```json
{"libelle":"GPS reorganisation"}
```

Remplacer V, A et E dans toutes les URL par les nombres retournés.
Les trois nouvelles opérations de relation ne demandent aucun corps JSON.

## Explication de chaque question

1. **Désaffecter un véhicule.** `desaffecterAgence(V)` charge le véhicule puis
   exécute `vehicule.setAgence(null)`. Le parent désigne ici l'agence.
   La transaction héritée de `GenericServiceImpl` permet à JPA de sauvegarder
   automatiquement cette modification, sans appel supplémentaire à `save`.
2. **API REST de désaffectation.** `DELETE /api/vehicules/V/agence` retourne
   HTTP 200 avec le véhicule modifié.
3. **Vérifier qu'il existe sans parent.** Commencer par
   `PUT /api/vehicules/V/agence/A`, puis désaffecter avec la requête du point 2.
   `GET /api/vehicules/V` doit retourner HTTP 200, le même `idVehicule` et
   `"agence": null`. L'agence existe toujours via `GET /api/agences/A`.
4. **Affecter une agence.** `affecterAgence(V, A)` recherche les deux entités puis
   renseigne `vehicule.agence`, côté propriétaire de la relation JPA.
5. **Tester avec deux identifiants dans l'URL.** Exécuter
   `PUT /api/vehicules/V/agence/A`. Attendre HTTP 200, puis vérifier dans
   `GET /api/vehicules/V` que `agence.idAgence` vaut A.
6. **Affecter un équipement.** `affecterEquipement(V, E)` ajoute l'équipement
   existant à `vehicule.equipements`. Un second appel identique ne crée pas de doublon.
7. **Vérifier l'ajout.** Exécuter `PUT /api/vehicules/V/equipements/E` : HTTP 200.
   Dans `GET /api/vehicules/V`, la liste `equipements` doit contenir un objet
   dont `idEquipement` vaut E.
8. **Supprimer le véhicule équipé et vérifier l'exception.** Exécuter
   `DELETE /api/vehicules/V` : HTTP 409. Le service lève une
   `DataIntegrityViolationException`, convertie en réponse JSON par le gestionnaire
   d'erreurs existant. `GET /api/vehicules/V` retourne encore HTTP 200.
   **Règle ajoutée pour cet exercice :** un véhicule ayant des équipements ne peut
   pas être supprimé. Le mapping ManyToMany existant, à lui seul, ne provoque pas
   cette exception : JPA sait supprimer les lignes de jointure côté véhicule.
9. **Supprimer l'équipement sans problème.** Exécuter
   `DELETE /api/equipements/E` : HTTP 204. Le service existant retire les associations
   avant de supprimer l'équipement. `GET /api/equipements/E` retourne HTTP 404 ;
   `GET /api/vehicules/V` retourne HTTP 200 avec `equipements: []` si E était son
   seul équipement. On peut ensuite supprimer le véhicule : HTTP 204, puis
   HTTP 404 à sa consultation.

Un identifiant inexistant sur les nouvelles opérations retourne HTTP 404.

## Tests automatisés

```powershell
.\mvnw.cmd test
# Uniquement cet exercice :
.\mvnw.cmd -Dtest=VehiculeReorganisationTests test
```

`VehiculeReorganisationTests` vérifie le service, les API via MockMvc, la persistance
après `flush` et vidage du contexte JPA, l'absence de doublons, les erreurs 404/409
et les suppressions. Les tests utilisent H2 en mémoire et annulent leurs données
à la fin de chaque test ; ils ne modifient pas MySQL.
