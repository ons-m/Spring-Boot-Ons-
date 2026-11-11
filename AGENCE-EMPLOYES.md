# Agence et employés — questions 10 à 19

## 10. Symétriser la relation

`Agence.ajouterEmploye(e)` et `Agence.retirerEmploye(e)` utilisent
`Employe.setAgence(a)`. Ce setter synchronise les deux côtés : il retire l'employé
de son ancienne agence et l'ajoute à la nouvelle, sans doublon. Le remplacement
de la liste via `setEmployes` utilise ces mêmes méthodes. La suppression d'une
agence parcourt une copie de la liste pour éviter de modifier la collection
pendant son parcours.

Vérification automatique : `relationSymetriqueAvecTransfertRetraitEtSansDoublon`.

## 11. Champ effectif

`Agence.effectif` est un `int`, mappé par `@Column(nullable = false)` dans la table
`agence`. Il est retourné dans le JSON et ne peut pas être fourni par le client.
La nouvelle opération le recalcule à partir de la taille totale de la liste des
employés, y compris ceux qui étaient déjà présents. Ce champ n'est pas une formule
SQL : il est recalculé par cette opération de service.

## 12. Service de mise à jour

`IAgenceService.updateAvecEmployes(id, agence, nouveauxEmployes)` :

1. Met à jour les informations de l'agence.
2. Appelle `IEmployeService.create` pour chaque nouvel employé.
3. Associe chaque employé créé à l'agence par la méthode de l'entité.
4. Affecte `agence.getEmployes().size()` à `effectif`.

L'API correspondante est `PUT /api/agences/{id}/employes`.
Les employés doivent être nouveaux, sans `idEmploye`.

## 13–14. Test Postman imbriqué et insertions

AutoLoc doit être démarré sur le port **8090**. Dans Postman, importer
`postman/Agence-Employes.postman_collection.json`, ouvrir la collection puis
**Run collection**. Exécuter toutes les requêtes dans l'ordre. La collection
crée sa propre agence, mémorise les identifiants et nettoie ses données à la fin.
Elle utilise sa variable `baseUrl`, avec la valeur `http://localhost:8090/api`.

Pour tester manuellement, créer d'abord une agence avec `POST /api/agences`,
puis utiliser son identifiant dans `PUT http://localhost:8090/api/agences/ID/employes`.
Sélectionner **Body → raw → JSON** et envoyer :

```json
{
  "agence": {
    "nom": "Agence transaction - valide",
    "ville": "Tunis",
    "adresse": "1 rue de Tunis",
    "telephone": "71000000"
  },
  "employes": [
    {"nom": "Ben Ali", "prenom": "Ali", "role": "AGENT"},
    {"nom": "Trabelsi", "prenom": "Sara", "role": "MANAGER"}
  ]
}
```

Résultat : HTTP **200**, `effectif: 2` pour une agence initialement sans employés.
`GET /api/agences/ID` confirme le nom et l'effectif. `GET /api/employes` montre
deux nouveaux employés avec `agence.idAgence` égal à ID.

On peut aussi vérifier MySQL :

```sql
SELECT id_agence, nom, effectif FROM agence WHERE id_agence = ID;
SELECT id_employe, nom, prenom, role, agence_id FROM employe WHERE agence_id = ID;
```

Remplacer ID par le nombre réel. Le compteur doit correspondre au nombre de lignes.

## 15–16. Deuxième employé invalide et incohérence

Remplacer le `nom` du deuxième employé par `""` et le nom de l'agence par
`"Modification a annuler"`. Le premier employé reste valide. Un nom vide est un
JSON syntaxiquement valide : c'est le **service employé** qui rejette les données.

Sans transaction commune, les opérations précédentes ont déjà été validées :
l'agence change, le premier employé est inséré, le second échoue et le calcul de
l'effectif n'est jamais atteint. L'effectif ne correspond donc plus aux employés.

Le test
`sansTransactionGlobaleLeDeuxiemeEmployeInvalideLaisseDesEcrituresPartielles`
reproduit réellement ce problème dans H2 : agence modifiée, un employé enregistré,
effectif resté à zéro. Il ne publie pas de route volontairement incorrecte.

## 17. Correction

La méthode `updateAvecEmployes` est annotée `@Transactional`. Les appels au
service employé participent à la même transaction Spring (propagation REQUIRED
par défaut). Une `IllegalArgumentException` sur le deuxième employé déclenche
le rollback de l'ensemble : modification de l'agence, première insertion et
modification de l'effectif.

La version finale retourne HTTP **400** pour le lot invalide. Après l'appel,
vérifier que le nom, l'effectif et le nombre d'employés sont exactement ceux
d'avant la requête. Dans la collection : le nom reste `Agence transaction - valide`,
l'effectif reste 2 et aucun troisième employé n'apparaît.

Les tests de transaction ne portent pas `@Transactional` : ils observent les vrais
commits et rollbacks du service, puis lisent les données avec des requêtes SQL.

## 18. Rendre les données valides

Remettre `"nom": "Trabelsi"` pour le deuxième employé et renvoyer la requête.
Résultat : HTTP **200**. Si l'agence avait déjà les deux employés du premier lot,
elle en possède maintenant quatre et `effectif` vaut **4**. Cette opération
ajoute des employés ; elle ne remplace pas ceux qui existent déjà.

## 19. Git et validation

```powershell
.\mvnw.cmd test
```

La suite contient 46 tests, dont les nouveaux tests d'entités, d'API, d'incohérence
sans transaction et de rollback. La collection Postman inclut des assertions
automatiques et le nettoyage. Les requêtes et assertions ont également été
exécutées contre AutoLoc/MySQL avec un exécuteur HTTP local ; l'interface graphique
Postman n'a pas été pilotée.

Les modifications de cet exercice et celles de la réorganisation des véhicules
sont regroupées dans le commit de cette session. Les suppressions d'images déjà
présentes dans l'index avant cette session sont exclues de ce commit.
