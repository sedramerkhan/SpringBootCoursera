# Domain & Data Models — Notes

Two complementary ways of modeling a system before any schema gets built: the
**domain model** (business-oriented, UML-flavored, no notion of a database
yet) and the **data model** (the conceptual → logical → physical progression
that leads directly to a schema). A domain model is effectively where a data
model's conceptual stage starts from — same idea, different vocabulary and
notation.

## Domain model

A **domain model** represents the concepts, relationships, and data within a
specific domain of interest. It abstracts real-world entities and their
interactions in the context of the system being designed — a conceptual
representation of the system's structure and behavior that bridges the real
world and the eventual system design.

### Why domain models matter

- **Shared understanding** — a clear, organized view of the system's
  components and relationships facilitates communication between
  stakeholders, developers, and designers, so everyone shares the same
  understanding of the domain.
- **Alignment** — that shared understanding keeps the system aligned with
  business requirements and user needs.
- **Blueprint for what comes next** — a domain model is the starting point
  for detailed system models and the database schema. It guides the logical
  and physical data models that follow (see [data model](#data-model) below),
  carrying the transition from conceptual design through to implementation.

### How to create one

1. **Identify key entities** in the domain, and define each entity's
   **attributes** and **behavior**.
2. **Establish relationships** between entities. Beyond simple
   [association](terminology-notes.md#relationship), two structural forms
   show up specifically in domain modeling:
   - **Association** — a general "uses/relates to" link between two
     independent entities (e.g. *customer places order*).
   - **Aggregation** — a "has-a" whole/part relationship where the part can
     exist independently of the whole (e.g. an `Order` *has* `Products`, but
     a `Product` still exists if the order is deleted).
   - **Composition** — a stricter "has-a" where the part's lifecycle is
     bound to the whole (e.g. an `Order`'s line items don't exist without the
     `Order`).
3. **Visualize with UML** (Unified Modeling Language) diagrams — the standard
   notation for representing a domain model's structure and interactions, so
   the design is easy to understand and communicate.

### Example — e-commerce domain model

- **Entities**: `Customer`, `Order`, `Product`, `Payment`, `Shipping`.
- **Attributes**: `Customer` — `CustomerID`, `name`, `email`; `Order` —
  `OrderID`, `OrderDate`.
- **Relationships**:
  - Customer **places** Order
  - Order **contains** Products
  - Payment **is made for** Order
  - Order **is shipped using** a Shipping method

This model captures how customers interact with the system, how orders are
processed, and how products are managed — before any table or column has
been designed.

### Domain models across industries

The same approach — entities, attributes, relationships — adapts to any
domain:

| Industry | Example entities |
|---|---|
| **Healthcare** | Patient, Doctor, Appointment, Prescription |
| **Education** | Student, Course, Enrollment, Instructor, Grade |
| **Finance** | Account, Transaction, Customer, Loan, Payment |

Each captures the unique aspects of its domain while following the same
modeling process.

## Data model

A **data model** represents the structure, relationships, and constraints of
the data in a database. It's the blueprint for designing and managing a
database, ensuring consistency and integrity, and abstracts/organizes data so
it reflects real-world scenarios and business requirements. A well-defined
data model is the common language that bridges business requirements and
technical implementation for stakeholders, developers, and DBAs alike.

### Three types of data model

| Type | View | Specifies | DBMS-specific? |
|---|---|---|---|
| **Conceptual** | High-level, business perspective | Key entities and relationships only | No |
| **Logical** | Detailed abstract structure | Entities, attributes, relationships, primary/foreign keys | No |
| **Physical** | Actual implementation | Tables, columns, data types, constraints, indexes/storage | Yes |

Each stage adds detail the one before it deliberately left out — a
conceptual model doesn't even name attributes yet (this is exactly the level
a [domain model](#domain-model) operates at); only the physical model
commits to a specific DBMS's data types and storage. This is the same
progression as the [design lifecycle](design-lifecycle-notes.md)'s conceptual
→ logical → physical stages — "data model" is the artifact produced at each
of those stages.

### Logical vs. physical, in detail

- **Logical data model** — the abstract structure: entities, attributes,
  relationships, independent of any specific DBMS, so it applies universally.
  An **[ERD](erd-notes.md)** (entity-relationship diagram) is a type
  of logical data model — it visualizes entities, attributes, and
  relationships so stakeholders can understand the data requirements without
  touching implementation details.
- **Physical data model** — the concrete implementation: specific tables,
  columns, data types, and constraints needed to build the database in a
  particular DBMS, tailored to that DBMS's capabilities/limits.

**Example** — a logical model might define an entity `Customer` with
attributes `customer_id` and `name`. The physical model turns that into:
`customer_id INT`, `name VARCHAR(255)` — plus indexing strategy and storage
parameters the logical model never addressed. That extra detail is what lets
the physical model be optimized for performance, storage, and retrieval in
the target database.

### Creating a data model — practical steps

1. **Identify entities and relationships** within the domain.
2. **Define attributes** for each entity, including primary and foreign keys.
3. **Create an ERD** to visualize the logical model — entities, attributes,
   relationships.
4. **Convert the ERD to a physical model** — define tables, columns, data
   types, and constraints, ensuring the physical design supports performance
   and storage requirements.

### Example — e-commerce system

**Entities & attributes:**

| Entity | Attributes | Keys |
|---|---|---|
| `Customer` | `customer_id`, `name`, `email` | PK `customer_id` |
| `Order` | `order_id`, `order_date`, `customer_id` | PK `order_id`, FK `customer_id` → `Customer` |
| `Product` | `product_id`, `name`, `price` | PK `product_id` |
| `Payment` | `payment_id`, `order_id`, `payment_date`, `amount` | PK `payment_id`, FK `order_id` → `Order` |

**Relationships:**

- **Customer places Order** — one customer can place many orders (`Order.customer_id` FK).
- **Order contains Products** — many-to-many, resolved via an `OrderDetail`
  junction table linking `order_id` and `product_id`.
- **Payments are made for Orders** — an order can have one or more payments
  (`Payment.order_id` FK).

### Example — healthcare system

| Entity | Attributes |
|---|---|
| `Patient` | `patient_id`, `name`, `date_of_birth` |
| `Doctor` | `doctor_id`, `name`, `specialization` |
| `Appointment` | `appointment_id`, `patient_id`, `doctor_id`, `date`, `type` |
| `Treatment` | `treatment_id`, `appointment_id`, `description`, `cost` |

Relationships: a patient can have multiple appointments; a doctor can conduct
multiple appointments; each appointment can involve one or more treatments —
all enforced via foreign keys (`Appointment.patient_id`,
`Appointment.doctor_id`, `Treatment.appointment_id`).

### Example — education system

Entities: `Student`, `Course`, `Enrollment`, `Instructor` — matching the
[design lifecycle](design-lifecycle-notes.md) and
[terminology](terminology-notes.md) examples already worked through
elsewhere. An instructor can teach multiple courses; each `Enrollment` record
links a student to a course (capturing performance), enforced via foreign
keys.

## Summary

- A **domain model** is a conceptual, DBMS-independent representation of a
  system's entities, attributes, relationships, and behavior — built via
  entities → attributes/behavior → relationships (association, aggregation,
  composition) → UML visualization.
- A **data model** = structure + relationships + constraints of the data,
  progressing through **conceptual** (entities/relationships only, the same
  level a domain model operates at) → **logical** (+ attributes, PK/FK,
  DBMS-independent, e.g. an [ERD](erd-notes.md)) → **physical**
  (+ tables/columns/data types/constraints, DBMS-specific).
- Both drive shared understanding across stakeholders/developers/DBAs and
  feed the same downstream artifact: a physical schema ready to implement.
- The same process (entities → attributes → relationships → ERD →
  physical schema) applies across e-commerce, healthcare, education, and
  finance domains — only the entities change.
