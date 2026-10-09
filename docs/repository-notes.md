# Notes de la couche Repository et SonarQube

## Choix des interfaces

| Interface | Étend | Justification |
| :--- | :--- | :--- |
| `IContratRepository` | `JpaRepository<Contrat, Long>` | CRUD complet, `findAll` retourne une `List`, tri, pagination et `saveAndFlush` disponibles. |
| `IAgenceRepository`, `IClientRepository`, `IEmployeRepository`, `IEquipementRepository`, `IMaintenanceRepository`, `IPaiementRepository`, `IReservationRepository`, `IVehiculeRepository` | `JpaRepository<Entité, Long>` | Uniformisation de la couche persistance pour bénéficier de toutes les fonctionnalités avancées de Spring Data JPA. |

## Anomalies SonarQube for IDE corrigées

| Anomalie SonarQube for IDE | Règle / Explication | Correction apportée |
| :--- | :--- | :--- |
| Utilisation de `@Data` sur une entité JPA | `@Data` génère `hashCode` et `equals` sur tous les champs, ce qui peut créer des boucles infinies avec les relations entre les tables. | Remplacement par `@Getter`, `@Setter` et un `@ToString` ciblé. |
| Import inutilisé | Une ligne `import` est présente dans le fichier mais n'est pas utilisée par le code. | Suppression de l'import inutile pour garder un code propre. |
| Code mort / Variable non utilisée | Présence d'une variable ou d'un paramètre déclaré mais jamais exploité. | Suppression de la variable superflue pour respecter les règles de Clean Code. |