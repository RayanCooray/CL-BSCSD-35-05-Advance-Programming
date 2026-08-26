# Sunrise Dental Clinic — Java 21 MVC Backend Layers

Java 21 + Swing project foundation based on the supplied class/sequence diagrams.

## Current scope

JavaFX desktop views are now included with separate UI controllers. The backend/domain layer remains separated from the UI:

```text
Controller
    ↓
Model
    ↓
DAO
    ↓
DAO Implementation
    ↓
MySQL
```

Also included:

- Entity classes
- DTO classes
- Lombok annotations on entities/DTOs
- DAO Factory
- Singleton database connection
- Password encoder
- Token utility
- MySQL schema
- JDBC implementations
- Validation/business logic
- No demo users
- No demo clinic data

## Lombok style

Entities and DTOs use:

```java
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Example {
}
```

## Database

Run:

```text
database/schema.sql
```

Then configure the connection in:

```text
src/main/java/lk/sunrise/dentalclinic/util/DatabaseConnection.java
```

## Java version

Java 21 LTS.

## Dentist management

The dentist backend now includes:

- `DentistDTO`
- `DentistDAO`
- `DentistDAOImpl`
- `DentistModel`
- `DentistController`
- Register dentist
- Update dentist
- Find by ID/code
- Search dentists
- Find available dentists
- Generate dentist code
- SLMC uniqueness validation
- Working-hours validation
- MySQL persistence through JDBC

## JavaFX UI

The desktop frontend uses JavaFX 21 with JavaFX CSS, ControlsFX notifications and Ikonli dependencies.

UI packages:

```text
lk.sunrise.dentalclinic.ui
├── DentalClinicApplication.java
├── Navigation.java
├── session/SessionContext.java
├── util/Ui.java
├── view/
│   ├── LoginView.java
│   ├── DashboardView.java
│   ├── DashboardHomeView.java
│   ├── PatientView.java
│   ├── DentistView.java
│   ├── TreatmentView.java
│   ├── AppointmentView.java
│   ├── TreatmentHistoryView.java
│   ├── BillingView.java
│   ├── ReportsView.java
│   └── UserManagementView.java
└── controller/
    ├── LoginViewController.java
    ├── DashboardViewController.java
    ├── PatientViewController.java
    ├── DentistViewController.java
    ├── TreatmentViewController.java
    ├── AppointmentViewController.java
    ├── TreatmentHistoryViewController.java
    ├── BillingViewController.java
    └── ReportsViewController.java
```

### Role-based rendering

- ADMIN: dashboard, patients, dentists, treatments, appointments, treatment history, billing, reports, users
- RECEPTIONIST: dashboard, patients, appointments, billing
- DENTIST: dashboard, patients, treatment history
- MANAGEMENT: dashboard, reports

Navigation is rendered from the authenticated role stored in `SessionContext`. The UI does not create demo users or demo records.

### Theme colors

The CSS follows the supplied design system: Primary `#2F9CCA`, Secondary `#384152`, Tertiary `#66748C`, Dark `#2F334A`, Base Background `#F2F5F9`, Table Heading `#F9FAFC`, Border `#D2D5DB`, Placeholder `#9BA1AE`, and CTA Hover `#237295`.

### Run

```bash
mvn clean javafx:run
```

Configure MySQL in `DatabaseConnection.java` and run `database/schema.sql` before signing in.
