 # Caching Strategies for APIs — Notes

Caching stores responses temporarily so the API doesn't reprocess the same
request repeatedly — making it faster, more scalable, and more reliable.

## Why cache

- **Performance** — reuse stored responses instead of processing the same request again.
- **Scalability** — fewer requests hit the server, so it supports more users without extra infrastructure.
- **Lower latency** — cached responses are served immediately, no full processing.
- **Cost efficiency** — less load on servers/databases means resources go further.
- **Reliability** — during traffic spikes or outages, cached responses keep services running.

## Types of caching

By *where* the cache lives, from closest to the client outward:

| Type | Where it sits | Purpose |
|---|---|---|
| **Client-side** | browser / app | reuse data locally without contacting the server again |
| **Server-side** | the API server | reuse responses instead of regenerating them, cutting processing load |
| **Reverse proxy** | intermediary (Nginx, Varnish) | store responses *before* they reach the API server |
| **CDN** | edge servers worldwide | serve from the nearest location to reduce latency |

### Reverse proxy caching, clarified

A **reverse proxy** is a server that sits **in front of your API** and receives
requests on its behalf — clients talk to the proxy, and it forwards to the API
only when needed. (It's "reverse" because a normal/forward proxy sits in front of
*clients*; this one fronts the *server*.) Tools: **Nginx**, **Varnish**, HAProxy.

- **How caching works:** the proxy stores the API's responses. On the next
  identical request it returns the stored copy **without touching the API server
  at all** — the request is answered before it ever reaches your app.
- **Why it helps:** the origin only regenerates a response on a cache miss, so it
  handles far less load and replies faster. It also centralizes caching for *all*
  clients (unlike client-side caching, which only helps one browser).
- **vs CDN:** same idea, different location. A reverse proxy usually lives in
  **your own infrastructure** (next to the API), while a CDN is **distributed
  globally** at the edge. A CDN is essentially a geographically spread set of
  reverse-proxy caches.
- **Analogy:** a receptionist who answers common questions from a cheat sheet and
  only escalates to the specialist (your API) for things not on the sheet.

### CDN caching, clarified

A **CDN** (Content Delivery Network) is a global network of **edge servers** — copies
of your content placed in data centers around the world. Instead of every user
reaching your single origin server (which might be one region away), each request
is served from the **geographically nearest** edge, so the data travels a much
shorter distance.

- **Why it helps:** distance = latency. A user in Tokyo hitting a server in
  Virginia waits on a round-trip across the planet; hitting a Tokyo edge instead
  cuts that to milliseconds. It also **offloads the origin** — most requests are
  answered at the edge and never reach your API server, so it handles far more
  traffic.
- **Best for static/cacheable content** — images, CSS/JS, videos, and API
  responses that don't change per user. Dynamic, user-specific responses are
  harder to cache at the edge (though TTLs and cache headers help).
- **How freshness works:** the edge keeps a copy governed by TTL / cache-control
  headers; when it expires (or you *purge* the CDN), the edge re-fetches from the
  origin. Same freshness trade-offs as below.
- **Analogy:** a global chain stocking the same product in local stores — you shop
  at the branch down the street instead of ordering from a single central
  warehouse overseas.

## Caching strategies

A cache is a small, fast store (usually in memory, e.g. Redis) sitting in front
of a slower store (the DB). The strategies differ in **who talks to what, and
when** — the first two are about **reads**, the last two about **writes**. They
aren't mutually exclusive: real systems pair one read strategy with one write
strategy.

### Cache-aside (a.k.a. "lazy loading") — read

The **app** is in charge. On a read: check the cache → **hit** returns the value;
**miss** means the app itself goes to the DB, gets the data, writes it into the
cache, then returns it. The cache is "on the side" — it knows nothing about the
DB; the app orchestrates everything. "Lazy loading" because data only enters the
cache when it's actually requested.

- **Trade-off:** most common and very flexible, but the app carries the cache
  logic everywhere it reads, and there's a **first-request penalty** (the first
  read of any item is always a miss that hits the DB).
- **Analogy:** checking your desk for a document before walking to the filing
  cabinet — if it's not there you fetch it and leave a copy on your desk for next
  time. You do all the walking yourself.

### Read-through — read

Same read logic, but the **cache** handles the miss, not the app. The app only
ever talks to the cache and never sees the DB: it asks the cache → hit returns;
on a miss the *cache* fetches from the DB, stores it, and returns it — the app
doesn't even know a miss happened.

- **Trade-off:** cleaner app code (one thing to talk to), but you need a cache
  system/library that supports read-through, since the cache must know how to load
  from the DB.
- **Analogy:** asking an assistant for a document — if they don't have it, they
  fetch it from the cabinet while you wait at your desk. You always deal with the
  assistant, never the cabinet.

> The read pair differs **only in who handles the miss**: your app code
> (cache-aside) or the cache layer (read-through).

### Write-through — write

On a write, data goes to the cache **and** the DB together, **synchronously** —
the write isn't complete until both are updated.

- **Trade-off:** cache and DB are always in sync, so later reads are guaranteed
  fresh (**strong consistency**). The cost: every write pays for two writes and
  waits for the slower one (the DB), so writes are a bit slower.
- **Analogy:** writing in your notebook and immediately filing the official copy
  in the cabinet before moving on — nothing is out of sync, but you can't leave
  until both are done.

### Write-back (a.k.a. "write-behind") — write

On a write, update **only the cache** and return immediately; the cache persists
to the DB **later, asynchronously** (batched or after a short delay).

- **Trade-off:** writes are very fast (touching only fast memory), but there's a
  window where data lives only in the cache — if it crashes then, that data is
  **lost**. Trades durability for speed.
- **Analogy:** jotting a note on a sticky pad to file at the end of the day — fast
  now, but if you lose the note before filing, it's gone.

> The write pair mirrors the read pair on a **consistency ↔ speed** axis:
> write-through does the slow thing now (safe); write-back defers it (fast, riskier).

## Invalidation & expiration

A cached copy is a snapshot — the moment the DB changes, it may be **stale**.
These are the **mechanisms** that keep the cache honest:

- **TTL (time to live)** — each entry auto-expires after a set duration. Simple;
  short TTL = fresher but more DB hits, long TTL = fewer hits but more staleness.
- **Manual invalidation** — explicitly clear/refresh the entry in the code that
  writes the data. Precise and immediate, but you must remember it everywhere.
- **Versioning** — bake a version into the cache key (`product:42:v3`); on change,
  bump to `v4`. Readers request the new key and miss to fresh data; you never
  delete the old entry, which sidesteps race conditions.

## Guiding goals (properties, not mechanisms)

These aren't things you implement — they're the **objectives** the choices above
serve, spanning both sections:

- **Freshness control** — deciding *how fresh is fresh enough* per data type, and
  tuning TTLs/strategies to that tolerance (a stock price needs seconds; country
  codes can cache for a day).
- **Consistency management** — keeping cache and DB in agreement. Write-through
  buys strong consistency for free; write-back and long TTLs mean *eventual*
  consistency you must design around.

> **Mechanisms vs properties:** strategies and invalidation methods are the
> concrete *mechanisms*; freshness and consistency are the *properties* they
> produce. This framing generalizes well beyond caching.

## Takeaway

Caching makes APIs fast, scalable, cost-effective, and dependable. Pick a read +
write **strategy** for the data flow, use **invalidation mechanisms** (TTL,
manual, versioning) to prevent stale data, and let your **freshness/consistency
goals** drive both choices.
