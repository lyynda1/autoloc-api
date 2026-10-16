# Note de synthèse - Atelier 4 : Inversion de Contrôle, Injection de Dépendances et Couche Service

## A. Modes d'injection comparés

| Critère | Injection par constructeur (Recommandé) | Injection par attribut (`@Autowired`) |
| :--- | :--- | :--- |
| **Champ `final`** | Oui (garantit l'immutabilité) | Non (champ mutable) |
| **Dépendance visible** | Oui (explicite dans la signature) | Non (cachée dans la classe) |
| **Utilisation hors conteneur** | Simple (`new Service(repo)`) | Impossible (dépendance `null`) |
| **Signalement SonarQube** | Aucun | Signalé (ex: règle S6813) |

---

## B. Tableau des services

| Service | Dépendances injectées | Mode d'injection | Justification |
| :--- | :--- | :--- | :--- |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur | Isolation de la logique métier et couplage faible |
| `ClientServiceImpl` | `IClientRepository` | Constructeur | Centralisation des règles d'identification client |
| `ContratServiceImpl` | `IContratRepository` | Constructeur | Gestion des contrats et cascade des paiements |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur | Validation des attributs obligatoires de l'employé |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur | Contrôle des libellés et quantités d'équipements |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur | Suivi des interventions et des dates de maintenance |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur | Exposition restreinte en lecture seule |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur | Validation de la cohérence des dates de réservation |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur | Contrôle des valeurs numériques (tarif journalier) |

---

## C. Messages d'erreur du conteneur

1. **`NoSuchBeanDefinitionException` :**
    * *Cause probable :* L'annotation `@Service` a été oubliée sur la classe d'implémentation, ou la classe se trouve en dehors du package de scan de Spring Boot (`tn.esprit.autoloc`).
    * *Correction :* Ajouter l'annotation `@Service` et s'assurer que la classe est placée dans le package `service.impl` sous le package racine.

2. **`NoUniqueBeanDefinitionException` :**
    * *Cause probable :* Le conteneur Spring trouve plusieurs beans candidats pour un même type de dépendance sans savoir lequel injecter.
    * *Correction :* Ajouter `@Primary` sur le bean prioritaire ou utiliser `@Qualifier("nomDuBean")` au niveau du point d'injection.

3. **Dépendance circulaire (`BeanCurrentlyInCreationException`) :**
    * *Cause probable :* Deux beans s'injectent mutuellement par constructeur, créant une boucle infinie à l'instanciation.
    * *Correction :* Revoir la conception de l'architecture pour extraire la logique commune dans un tiers composant ou éviter le couplage bidirectionnel entre services.

---

## D. Anomalies SonarQube for IDE

1. **Règle S6813 (Injection par attribut) :**
    * *Explication :* L'utilisation de `@Autowired` sur les champs induit un couplage fort et des dépendances cachées.
    * *Correction :* Remplacement par l'injection par constructeur avec des champs `final` et l'annotation Lombok `@RequiredArgsConstructor`.

2. **Règle S112 (Exception générique) :**
    * *Explication :* Le code levait une `RuntimeException` générique au lieu d'une exception métier typée.
    * *Correction :* Utilisation de l'exception dédiée `ResourceNotFoundException` pour une meilleure explicite sémantique.

3. **Imports inutiles :**
    * *Explication :* Présence de déclarations d'imports non référencées dans le corps de certaines classes de services.
    * *Correction :* Nettoyage et suppression automatique des imports superflus.

---

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**
   Il n'est pas écrit explicitement dans le code source. C'est le conteneur Spring (`ApplicationContext`) qui l'exécute dynamiquement au démarrage de l'application lors de la phase d'instanciation des beans.

2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**
   Pour respecter le principe d'inversion des dépendances (du SOLID) : le code de haut niveau doit dépendre d'une abstraction (l'interface) et non d'une implémentation concrète, favorisant la modularité et l'évolutivité.

3. **Pourquoi un service singleton doit-il rester sans état ?**
   Puisqu'une seule instance est partagée entre tous les threads et requêtes simultanées, y stocker un état (un attribut modifiable propre à un appel) provoquerait des bugs de concurrence imprévisibles (*race conditions*).

4. **Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?**
   Pour éviter d'écraser des colonnes non fournies par le client avec des valeurs `null` et pour préserver l'intégrité des relations rattachées (comme la suppression en cascade ou le vidage accidentel de collections).

5. **Quelle différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?**
    * `@Component` s'applique au niveau de la classe pour un scan automatique, tandis que `@Bean` s'utilise sur une méthode au sein d'une classe de configuration pour déclarer un objet manuellement.
    * `@Primary` définit un choix par défaut global pour un type, alors que `@Qualifier` permet de cibler explicitement un bean par son nom.
    * `IPaiementService` est en lecture seule car la gestion et le cycle de vie des paiements sont pilotés de manière transactionnelle et intégrée directement au sein du service de gestion des Contrats.