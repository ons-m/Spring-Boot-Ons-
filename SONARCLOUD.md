# Analyse manuelle SonarQube Cloud

## 1. Envoyer le code sur GitHub

Le dépôt GitHub utilisé par le projet est :

`https://github.com/ons-m/Spring-Boot-Ons-`

Depuis la racine du projet :

```powershell
git add .
git commit -m "chore: preparer l analyse SonarQube Cloud"
git push origin main
```

Les fichiers ou suppressions déjà présents dans l'index doivent être vérifiés avant le commit.

## 2. Créer le projet SonarQube Cloud

1. Ouvrir [SonarQube Cloud](https://sonarcloud.io).
2. Se connecter avec le compte GitHub.
3. Cliquer sur `+` puis `Analyze new project`.
4. Sélectionner l'organisation GitHub et le dépôt `Spring-Boot-Ons-`.
5. Cliquer sur `Set up`, puis `Create project`.
6. Noter les valeurs `organization key` et `project key`.

## 3. Créer le token

Dans SonarQube Cloud : `Account → My Account → Security → Generate token`.

Le token ne doit pas être ajouté dans Git, dans `pom.xml` ou dans ce fichier.

Dans PowerShell, le token peut être défini uniquement pour la session courante :

```powershell
$env:SONAR_TOKEN = "TON_TOKEN_SONARCLOUD"
```

## 4. Lancer l'analyse depuis la machine locale

Exécuter la commande depuis le dossier contenant `pom.xml` :

```powershell
.\mvnw.cmd clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar `
  "-Dsonar.host.url=https://sonarcloud.io" `
  "-Dsonar.organization=ORGANIZATION_KEY" `
  "-Dsonar.projectKey=PROJECT_KEY" `
  "-Dsonar.token=$env:SONAR_TOKEN"
```

Remplacer `ORGANIZATION_KEY` et `PROJECT_KEY` par les valeurs affichées par SonarQube Cloud.

La commande compile le projet, exécute les tests, analyse `src/main/java` et `src/test/java`, puis envoie le résultat à SonarQube Cloud.

## 5. Vérifier l'envoi

À la fin de la commande, ouvrir le lien SonarQube Cloud affiché dans le terminal. Vérifier :

- le nombre de bugs ;
- les vulnérabilités et security hotspots ;
- les code smells ;
- la couverture des tests ;
- les duplications ;
- le statut du Quality Gate ;
- la date et le numéro de la dernière analyse.

## 6. Corriger puis réanalyser

Après chaque correction :

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar `
  "-Dsonar.host.url=https://sonarcloud.io" `
  "-Dsonar.organization=ORGANIZATION_KEY" `
  "-Dsonar.projectKey=PROJECT_KEY" `
  "-Dsonar.token=$env:SONAR_TOKEN"
```

Vérifier que les métriques et le Quality Gate se sont améliorés.

## 7. Envoyer les corrections finales

```powershell
git add .
git commit -m "fix: corriger les anomalies SonarQube Cloud"
git push origin main
```
