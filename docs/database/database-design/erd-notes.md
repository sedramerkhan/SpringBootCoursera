# Entity-Relationship Diagrams (ERDs) — Notes

An **ERD** (entity-relationship diagram) — also called an "ER diagram" or
just "ERD", used interchangeably — is a type of flowchart illustrating how
entities relate to each other within a system. It's the visual representation
of a database's [logical data model](modeling-notes.md#data-model), showing
how tables (entities) interconnect through relationships.

ERDs are most valuable at the **initial stage** of database design: they
give a high-level view of the system structure and surface potential issues
or inconsistencies early, before implementation, saving time and rework
later — the visual counterpart of the
[conceptual/logical design stages](design-lifecycle-notes.md#2-conceptual-design).

## Basic notation

| Component | Shape | Meaning |
|---|---|---|
| **Entity** | Rectangle | An object/concept data is stored about (e.g. `Instructor`, `Course`) |
| **Attribute** | Oval, connected to its entity | A property of that entity (e.g. `Instructor` → `ID`, `name`, `salary`) |
| **Relationship** | Diamond, connected by lines to the entities involved | How two entities interact (e.g. `Instructor` **advises** `Student`) |

These map onto the same [entity/attribute/relationship](terminology-notes.md)
vocabulary already covered — an ERD is just their standard visual notation.

### Entity sets

An **entity set** is a collection of similar entities — e.g. all students in
a university form the entity set `Student`. In the diagram:

- The entity set is a rectangle; its attributes are listed **inside** the
  rectangle (rather than as separate connected ovals, in this compact form).
- The **primary key** attribute is **underlined** to mark its unique-identifying
  role — e.g. `Student` lists `student_id` (underlined), `name`,
  `total_credit`.

### Attributes on a relationship

A relationship can carry its own attribute when the relationship itself has
data worth capturing. E.g. the `advises` relationship between `Instructor`
and `Student` might carry a `date` attribute (when the advising started) —
information that belongs to neither entity alone, only to their connection.

```
       (ID)      (name)     (salary)                         (date)
         \          |          /                                |
          \         |         /                                 |
        ┌──────────────────────┐                          ┌───────────┐
        │      INSTRUCTOR      │                           (relationship
        └──────────┬───────────┘                            attribute)
                   │                                              │
                   │                                              │
                   │              ┌──────────┐                    │
                   └──────────────│ advises  │────────────────────┘
                                  └────┬─────┘
                                       │
                                       │
                              ┌──────────────────┐
                              │      STUDENT      │
                              └──────────┬─────────┘
                                /        │        \
                               /         │         \
                          (ID)      (name)   (total_credit)
```

`INSTRUCTOR` and `STUDENT` are entities (rectangles) with their own
attributes (ovals, `ID` underlined as each entity's PK). They connect through
the `advises` relationship (diamond), which carries its own attribute,
`date` — data that belongs to the relationship itself, not to either entity.

## Example — e-commerce system

Entities: `Customer`, `Order`, `Product` — the same trio used in the
[data model notes](modeling-notes.md#example--e-commerce-system):

- `Customer` — `customer_id`, `customer_name`, `email`
- `Order` — `order_id`, `order_date`, `customer_id`
- `Product` — `product_id`, `product_name`, `price`

Relationships:

- **Customer places Order** — **one-to-many**: one customer can place many
  orders.
- **Order contains Products** — **many-to-many**: an order can contain
  multiple products, and a product can appear on multiple orders.

## Creating an ERD

1. **Identify entities** — the main objects/concepts in the domain (e.g.
   `Instructor`, `Student`).
2. **Define attributes** — the properties describing each entity (e.g.
   `Instructor` → `ID`, `name`, `salary`).
3. **Establish relationships** — how entities connect (e.g. `Instructor`
   **advises** `Student`), drawn as a diamond linking the two rectangles.
4. **Draw the diagram** — rectangles for entities, ovals for attributes,
   diamonds for relationships, connected by lines. [UML](modeling-notes.md#how-to-create-one)
   is a common notation for producing a clean, organized ERD.

## Advanced notation

Richer attribute types, precise cardinality/participation notation, and weak
entity sets — the detail a basic ERD glosses over.

### Attribute types

| Type | Meaning | Example |
|---|---|---|
| **Simple** | Single-valued — one value per entity | `SSN`, `date_of_birth` |
| **Composite** | Made of sub-parts, each meaningful on its own | `address` → `street`, `city`, `state`, `zip` |
| **Multivalued** | An entity can have more than one value | `phone_numbers` (a person can have several) |
| **Derived** | Computed from another attribute, not stored | `age` derived from `date_of_birth` |

- **Composite** attributes are drawn as an oval for the whole attribute
  (`address`), with its sub-part ovals connected underneath it (`street`,
  `street_number`, `apartment_number`, ...).
- **Derived** attributes are drawn distinctly (e.g. a dashed oval) to signal
  they're computed on the fly, not stored — avoiding redundant data while
  still capturing the value in the model. This is the same
  [normalization](../advanced-sql/normalization-notes.md) instinct — don't
  store what you can derive.

### Cardinality

**Cardinality** specifies how many instances of one entity can/must
associate with each instance of another. Four types:

| Cardinality | Rule | Example |
|---|---|---|
| **One-to-one (1:1)** | Each A relates to at most one B, and vice versa | Each person has exactly one passport |
| **One-to-many (1:N)** | One A relates to many B, but each B relates to only one A | One teacher teaches many courses; each course has one teacher |
| **Many-to-one (N:1)** | Same as 1:N, direction reversed | Many employees work in one department |
| **Many-to-many (N:N)** | Many A relate to many B, and vice versa | A student enrolls in many courses; a course has many students |

**Notation:**

- **1:1** — a line with a single arrowhead pointing at **each** entity.
- **1:N** — a single arrowhead pointing at the "one" side, an undirected line
  on the "many" side. (**N:1** is the same drawing, mirrored.)
- **N:N** — an undirected line on **both** sides.

### Participation constraints

Alongside cardinality, a line also encodes whether membership is mandatory:

| Participation | Line | Meaning |
|---|---|---|
| **Total** | Double line | Every entity in the set **must** participate in the relationship |
| **Partial** | Single line | Only **some** entities in the set participate — optional |

**Example — `Student`–`enrolled`–`Course`:**

```
                    ┌────────────┐
                    │  STUDENT   │
                    └──────┬─────┘
                           ║           <- double line: TOTAL participation
                           ║
                      ╱────┴────╲
                     ╱  enrolled  ╲
                     ╲            ╱
                      ╲────┬────╱
                           │            <- single line: PARTIAL participation
                           │
                    ┌──────┴─────┐
                    │   COURSE   │
                    └────────────┘
```

- **`Course`'s participation is partial** (single line) — a course may or may
  not have any students enrolled in it. It's possible only *some* `Course`
  entities are related to the `Student` set through `enrolled`.
- **`Student`'s participation is total** (double line) — every student is
  expected to relate to **at least one** course through `enrolled`; no
  student entity can exist outside the relationship.

The asymmetry is the point: participation is judged **per entity set**, not
per relationship as a whole — one side can be mandatory while the other is
optional.

### Weak entity sets

A **weak entity** can't be uniquely identified by its own attributes alone —
it depends on a **strong (owner) entity** for identification. It represents
something that's logically dependent on another object.

**Example**: a `Section` (of a course) is a weak entity — it only makes
sense, and is only identifiable, in the context of the `Course` it belongs
to.

Notation:

- **Double rectangle** — the weak entity itself (vs. a single rectangle for
  a strong entity).
- **Discriminator attribute** — shown with a **dashed underline**; the
  attribute that, combined with the owner's key, uniquely identifies the
  weak entity (e.g. `Section` might use `section_number` as its
  discriminator — unique *within* a given course, not globally).
- **Double diamond** — the relationship connecting the weak entity to its
  owner, emphasizing the dependency.

```
                                  ┌────────────┐
                                  │   COURSE   │  (strong entity)
                                  └──────┬─────┘
                                         │
                                    ╔════╧════╗
                                    ║ belongs ║  (double diamond)
                                    ║   _to   ║
                                    ╚════╤════╝
                                         │
                                 ┌───────┴────────┐
                                 ║    SECTION     ║  (double rectangle — weak entity)
                                 └───────┬────────┘
                                         │
                                (section_number)  ← dashed underline (discriminator)
```

## Extended ER (EER) models

The **basic ER model** (everything above) covers entities, attributes,
relationships, cardinalities, and weak/strong entity dependency. The
**extended ER (EER) model** adds three more components — **generalization**,
**specialization**, and **aggregation** — for a more detailed and accurate
representation of complex scenarios the basic model can't express precisely.

| Component | Direction | What it does |
|---|---|---|
| **Generalization** | Bottom-up | Abstracts common features from multiple specialized entities into one generalized entity |
| **Specialization** | Top-down | Splits a generalized entity into more specific sub-entities, each adding its own attributes/relationships |
| **Aggregation** | — | Treats a relationship set as if it were an entity set, so it can participate in further relationships |

### Specialization

A **top-down design** technique: starting from a generalized (high-level)
entity, designate **sub-groups** that capture the differences between the
entities it contains. A sub-group's entities have attributes, or participate
in relationships, that don't apply to the generalized entity as a whole —
specialization exists precisely to represent those differences without
forcing every entity into one flat, generic shape.

**Overlapping vs. disjoint** — can an entity belong to more than one
sub-group?

| Constraint | Rule | Example |
|---|---|---|
| **Overlapping** | An entity can belong to more than one specialized group | A person can be both an `Employee` and a `Student` |
| **Disjoint** | An entity belongs to at most one specialized group | An `Employee` is either an `Instructor` or a `Secretary`, never both |

**Total vs. partial** — must every high-level entity belong to *some*
sub-group?

| Constraint | Rule | Notation |
|---|---|---|
| **Total specialization** | Every entity in the high-level set must belong to one of the low-level sets | Double line, high-level entity → specialized entity |
| **Partial specialization** | Some entities in the high-level set belong to no low-level set | Single line, high-level entity → specialized entity |

### Generalization

The **bottom-up** inverse of specialization: multiple specialized entities
are combined into a single generalized entity, by identifying their common
features and abstracting them into a high-level entity capturing the shared
attributes/relationships. E.g. `Car` and `Truck` might both generalize into
a `Vehicle` entity capturing shared attributes like `vehicle_id` and
`manufacturer`.

Generalization has the same total/partial split, from the generalized
entity's side:

| Constraint | Rule | Example |
|---|---|---|
| **Total generalization** | Every entity in the high-level set must belong to one of the low-level sets | All `Employee`s must be either `Manager` or `Staff` |
| **Partial generalization** | Some entities in the high-level set may not belong to any low-level set | Some `Vehicle`s may not be categorized as either `Car` or `Truck` |

### Aggregation

A high-level abstraction that treats a **relationship set** as an **entity
set**. This lets the relationship be associated with *other* relationships —
representing complex, multi-step interactions — without introducing
redundant new entities, and keeps the model modular.

**Example**: a `Project` relationship involving `Student` and `Instructor`
can itself be abstracted into an entity. That abstracted `Project` can then
participate in a further relationship — e.g. `ProjectFunding`, linking it to
a `FundingAgency` entity — which a flat, non-aggregated relationship
couldn't directly support.

### EER notation summary

| Element | Symbol |
|---|---|
| Entity | Rectangle |
| Attribute | Oval |
| Relationship | Diamond |
| **Aggregate entity** (a relationship treated as an entity) | Double diamond |
| **Generalization / specialization hierarchy** | Triangle, connecting the high-level entity to its sub-groups |

### Creating an EER model

1. **Identify entities and relationships** — the basic building blocks. E.g.
   in a university database: entities `Student`, `Instructor`, `Course`;
   relationships `enrolls` (`Student`–`Course`) and `teaches`
   (`Instructor`–`Course`).
2. **Apply generalization/specialization** — look for attributes or
   relationships shared across entities and abstract them into a general
   entity; then define what makes each specialized entity distinct. E.g.
   generalize `Student` and `Instructor` into a `Person` entity holding
   shared attributes (`name`, `address`); `Student` keeps its own
   `student_id`/`major`, `Instructor` keeps `instructor_id`/`department`.
3. **Apply aggregation** — identify relationships that can be abstracted
   into high-level entities to cut redundancy and simplify the model. E.g.
   a `Project` relationship (`Student`–`Instructor`) becomes its own entity
   so it can participate in a further relationship like `ProjectFunding`.
4. **Draw the diagram** — rectangles for entities, ovals for attributes,
   diamonds for relationships, **double diamonds** for aggregate entities,
   and **triangles** for generalization/specialization hierarchies.

## Building ERDs in MySQL Workbench

**From an existing database** (reverse engineering):

1. Open MySQL Workbench → **Database** → **Reverse Engineer**.
2. Choose the database to reverse-engineer.
3. Follow the prompts — Workbench generates the ERD from the existing schema
   automatically.

Reverse engineering is useful for documenting and understanding a database
that already exists, and for spotting areas to improve.

**From scratch:**

1. **File** → **New Model**.
2. Right-click the canvas → **Place a New Table**; name it and add columns
   with their data types (e.g. `Student`, `Course`, `Enrollment`,
   `Instructor`).
3. Define primary keys, foreign keys, and other constraints on each table.
4. Repeat for every table the design needs.

**Defining relationships between tables:**

1. Use the foreign key tool to relate two tables.
2. Set the foreign key name, the referenced table, and the referenced
   column.
3. Set the relationship type — one-to-one, one-to-many, or many-to-many.
4. Confirm the FK column and constraints are represented accurately in the
   diagram.

Workbench isn't required — any ERD tool works, and even pen and paper is
fine for early sketches.

## Summary

- An ERD visually documents a database's entities, attributes, and
  relationships — rectangles, ovals, and diamonds respectively. An **entity
  set** groups similar entities; its primary key is underlined.
- Attributes can be **simple**, **composite** (with sub-parts), or
  **derived** (computed, not stored). Relationships can themselves carry
  attributes when the connection itself holds data.
- **Cardinality** (1:1, 1:N, N:1, N:N) is drawn with arrowheads; a separate
  **participation** constraint (double line = total/mandatory, single line =
  partial/optional) is judged per entity set.
- A **weak entity** (double rectangle) can't stand on its own — it's
  identified via a **discriminator** (dashed underline) plus a **double
  diamond** relationship to its owning strong entity.
- **Extended ER (EER)** adds **specialization** (top-down; can be
  overlapping/disjoint and total/partial), its inverse **generalization**
  (bottom-up; also total/partial), and **aggregation** (relationship treated
  as an entity, so it can join further relationships) — drawn with
  **triangles** (generalization/specialization hierarchy) and **double
  diamonds** (aggregate entities).
- Build an EER model by: identify entities/relationships → apply
  generalization/specialization to abstract shared vs. distinct attributes →
  apply aggregation to relationships worth reusing → draw the diagram.
- Build a plain ERD by: identify entities → define attributes → establish
  relationships → draw (or generate via MySQL Workbench, from an existing
  DB via reverse engineering, or from scratch).
