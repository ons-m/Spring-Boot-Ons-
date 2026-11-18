# Impact Faible

* Remove the 'ApiResponses' wrapper from this annotation group [java:S1710]
* Rename this generic name to match the regular expression '^[A-Z][0-9]?$'. [java:S119]
  j'ai supprimer les wrappers @ApiResponses et renommer les types génériques selon la convention Sonar.
# Impact Moyen

* Add at least one assertion to this test case. [java:S2699]
* Refactor the code of the lambda to have only one invocation possibly throwing a runtime exception. [java:S5778]
* Call transactional methods via an injected dependency instead of directly via 'this'. [java:S6809]
* Refactor this method to reduce the number of assertions from 28 to less than 25. [java:S5961]
* Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method. [java:S8688]
  Pour les anomalies d’impact moyen, j’ai corrigé 5 types :

AgenceTests.java — ajout d’assertions manquantes [java:S2699].
AgenceEmployesTests.java — préparation de l’employé invalide avant assertThrows [java:S5778].
AgenceServiceImpl.java — appel de update() via une dépendance injectée [java:S6809].
AutolocApiApplicationTests.java — séparation de la méthode avec trop d’assertions [java:S5961].
GlobalExceptionHandler.java — ajout du fuseau horaire avec ZoneId.systemDefault() [java:S8688].
# Impact Important

* Replace this persistent entity with a simple POJO or DTO object. [java:S4684]
* Replace this use of System.out by a logger. [java:S106]

## Total

64 anomalies detected in 19 files. Each type of anomaly is listed once, without repetition.

After correcting the low-impact anomalies, 27 anomalies remain.
