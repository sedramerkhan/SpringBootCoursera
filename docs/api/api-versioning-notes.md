# API Versioning — Notes

A short reference on managing change in an API without breaking existing clients.

## What it is

**API versioning** is the practice of managing and identifying changes in an API
**without breaking existing client integrations**. It's a structured way to ship
updates while keeping things stable for current users.

The core idea: when an API changes, developers on the older version shouldn't
suddenly find their apps failing. Versioning lets them keep working against the
existing version while new features land in a newer one — balancing **stability**
with **innovation**.

## Why it matters

- **Backward compatibility** — older clients keep working when new updates ship;
  without it, existing apps break the moment the API changes.
- **Change management** — new features and bug fixes roll out gradually, without
  disrupting users still on older versions.
- **Controlled evolution** — clients always know which version they're on, giving
  confidence and clarity when building or maintaining apps.
- **Clear communication** — a smooth transition path between old and new lets
  providers and users plan upgrades without surprises.

## Strategies

| Strategy | How the version is passed | Pros | Cons |
|---|---|---|---|
| **URI / path** | in the URL, e.g. `/api/v1/users` | clear, visible right in the path, easy to understand | can lead to duplicate endpoints per version → extra maintenance |
| **Query parameter** | as a query param, e.g. `/api/users?version=1` | flexible, multiple versions without changing the endpoint path | less visible; clutters the query string if overused |
| **Header** | in a custom request header | keeps the URL clean and uncluttered | clients must remember to set the header, or the request fails |
| **Content negotiation** | in the media type of the `Accept` header | great flexibility — different versions of the same resource on one endpoint | more complex to manage consistently across clients and servers |

Rule of thumb: **URI/path** and **query parameter** are the most widely used;
the choice depends on how *simple* vs *flexible* the API needs to be.
**Header** emphasizes clean URLs; **content negotiation** emphasizes flexibility —
both need careful implementation.

## Approaches in practice

How each looks on the wire, and where it fits:

### URI / path versioning
```
GET /api/v1/users     →     GET /api/v2/users
```
Version sits directly in the URL. Simple to implement and maintain, clearly
visible, and the standard choice for RESTful APIs. Trade-off: each version is a
new endpoint, so the same resource ends up with multiple paths. The most
straightforward, developer-friendly option.

### Query parameter versioning
```
GET /api/users?version=1     →     GET /api/users?version=2
```
Version passed as a query param; the base URL stays clean while supporting
multiple versions. Easy to test and toggle between versions during development.
Trade-off: less visible than the path, and the query string can get cluttered if
many params pile up.

### Header-based versioning
```
GET /api/users
API-Version: 1          →      API-Version: 2
```
Version travels in a custom HTTP request header instead of the URL — bump the
version by changing the header value. Keeps URLs clean and consistent across
versions, so it suits large, enterprise-level APIs needing flexible control.
Trade-off: clients must remember to set the header, or the request won't hit the
intended version — it takes more developer discipline.

### Content negotiation versioning
```
GET /api/users
Accept: application/vnd.myapp.v1+json    →    ...vnd.myapp.v2+json
```
Version specified inside the `Accept` header via the media type. Clients can
request different representations of the same resource on demand — powerful for
complex APIs needing fine-grained control. Trade-off: hardest to implement and
manage consistently, especially with many versions/representations. Mostly used
in advanced, enterprise-level APIs.

**Bottom line:** URI path is simple and visible; header-based keeps URLs clean;
content negotiation gives the most flexibility. The right choice depends on your
API's audience and the level of control you need.

## Workflow for managing versions

1. **Choose a strategy** — decide up front how versioning is handled (URI, query
   param, or header). This sets how clients interact with different versions.
2. **Create a new version only when breaking changes are necessary** — if small
   updates can ship without disruption, a new version isn't always required.
3. **Update documentation** — clearly describe what changed, what's new, and what
   clients need to do differently.
4. **Gradual deployment** — roll the new version out *alongside* the old one, so
   clients can migrate at their own pace.
5. **Deprecate the old version** — once most clients have switched, phase out the
   outdated version in a controlled way.

## Managing multiple versions

Once you've shipped v2, v1 usually can't disappear overnight — you run several
versions at once. Why this matters:

- **Backward compatibility** — a mobile app built on v1 must keep working after
  v2 ships.
- **Diverse clients** — different apps, devices, and partners may each depend on
  a different version.
- **Smooth feature rollout** — introduce new features without breaking existing systems.
- **Controlled deprecation** — retire old versions gradually with clear timelines
  so developers have time to migrate; builds trust.

### Strategies for maintaining versions

| Strategy | What it means |
|---|---|
| **Parallel support** | keep old and new versions active at the same time; clients migrate at their own pace |
| **Long-term support (LTS)** | maintain stable versions longer for enterprise users who can't upgrade quickly |
| **Feature flags** | toggle individual features on/off per client instead of spinning up a whole new version |
| **Deprecation planning** | set clear timelines and guidance so clients know when to migrate and what to expect |

### Deprecation vs sunset policies

- **Deprecation policy** — *announce* retirement well in advance. Provide clear
  migration paths and updated docs; **backward compatibility is still maintained**
  during the transition so existing apps don't break.
- **Sunset policy** — *permanently retires* an unsupported version and marks its
  endpoints as fully deprecated, while offering stable alternatives to migrate to.

Together they let the API evolve **predictably**, without surprising the
developers who depend on it.

### Communication best practices

Communication matters as much as the technical work:

1. **Clear documentation** — updated references explaining what changed.
2. **Changelogs** — what was added, modified, or removed per version.
3. **Migration guides** — step-by-step old → new instructions.
4. **Early notifications** — warn about breaking changes well ahead of time.
5. **Developer support** — FAQs, forums, dedicated channels during migration.
6. **Transparency** — open communication builds trust and confidence in the API.

## Quick mental model

Versioning = balancing **innovation** with **stability**: prevent breaking
changes, ensure smooth transitions, and let the API keep improving for the future
without disrupting the clients already relying on it.
